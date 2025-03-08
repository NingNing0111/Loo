use bytes::Bytes;
use prost::Message;
use std::sync::Arc;
use tauri::{AppHandle, Emitter, EventTarget};
use tokio::{
    io::{AsyncReadExt, AsyncWriteExt},
    net::{
        tcp::{OwnedReadHalf, OwnedWriteHalf},
        TcpStream,
    },
    select,
    sync::{broadcast, mpsc, Mutex, RwLock},
};

use crate::{
    common::constants::{LICENSE_KEY, SERVER_ERROR_CODE, VISITOR_ID},
    core::{cmd_type::CmdType, transfer_message::TransferDataMessage},
    helper::message::{
        build_auth_message, build_connect_message, build_disconnect_message,
        build_open_server_message, build_transfer_message,
    },
    model::{command::CommandResult, proxy::ProxyConfig, ClientConfig},
};

pub mod manager;
use anyhow::{Context, Result};
use manager::LocalManager;

pub struct ClientApp {
    config: ClientConfig,
    local_manager: Arc<RwLock<LocalManager>>, // 用 RwLock 替代 Mutex
    shutdown_tx: broadcast::Sender<()>,       // 无需 Arc
    s_tx: mpsc::Sender<TransferDataMessage>,
    s_rx: Arc<Mutex<mpsc::Receiver<TransferDataMessage>>>,
    r_tx: mpsc::Sender<TransferDataMessage>,
    r_rx: Arc<Mutex<mpsc::Receiver<TransferDataMessage>>>,
    s_writer: Arc<Mutex<Option<OwnedWriteHalf>>>,
    s_reader: Arc<Mutex<Option<OwnedReadHalf>>>,
    e_tx: mpsc::Sender<CommandResult<()>>,
    e_rx: Arc<Mutex<mpsc::Receiver<CommandResult<()>>>>,
}

impl ClientApp {
    pub fn new(config: ClientConfig) -> Self {
        let (shutdown_tx, _) = broadcast::channel(4);
        let (s_tx, s_rx) = mpsc::channel::<TransferDataMessage>(32);
        let (r_tx, r_rx) = mpsc::channel::<TransferDataMessage>(32);
        let (e_tx, e_rx) = mpsc::channel::<CommandResult<()>>(4);
        ClientApp {
            config,
            local_manager: Arc::new(RwLock::new(LocalManager::new())),
            shutdown_tx,
            s_tx,
            s_rx: Arc::new(Mutex::new(s_rx)),
            r_tx,
            r_rx: Arc::new(Mutex::new(r_rx)),
            s_writer: Arc::new(Mutex::new(None)),
            s_reader: Arc::new(Mutex::new(None)),
            e_tx,
            e_rx: Arc::new(Mutex::new(e_rx)),
        }
    }

    pub async fn start(&mut self) -> Result<()> {
        let _ = self.connect().await;

        let _ = self.listen_read_data().await;
        let _ = self.listen_write_data().await;

        let _ = self.handler().await;

        Ok(())
    }

    async fn connect(&mut self) -> Result<()> {
        let server_host = self.config.get_server_host().to_string();
        let server_port = self.config.get_server_port();
        let server_addr = format!("{}:{}", server_host, server_port);
        let server_channel = match TcpStream::connect(&server_addr).await {
            Ok(server_channel) => server_channel,
            Err(e) => {
                self.e_tx
                    .send(CommandResult::custom_err(
                        "无法与服务端建立连接",
                        SERVER_ERROR_CODE,
                    ))
                    .await
                    .unwrap();
                return Err(e.into());
            }
        };

        let (s_reader, s_writer) = server_channel.into_split();
        {
            let mut s_writer_lock = self.s_writer.lock().await;
            *s_writer_lock = Some(s_writer);
            let mut s_reader_lock = self.s_reader.lock().await;
            *s_reader_lock = Some(s_reader)
        }

        Ok(())
    }

    /// 监听要发送到服务端的数据
    async fn listen_write_data(&mut self) {
        let mut shutdown_rx = self.shutdown_tx.subscribe();
        let s_rx = Arc::clone(&self.s_rx); // 克隆 Arc 以移动到异步任务
        let s_writer = Arc::clone(&self.s_writer);
        log::info!("正在监听写入到服务端的数据...");
        let e_tx = self.e_tx.clone();
        tokio::spawn(async move {
            loop {
                select! {
                    msg = async {
                        let mut s_rx_lock = s_rx.lock().await;
                        s_rx_lock.recv().await
                    } => {
                        if let Some(msg) = msg {
                            let w_msg = msg.encode_length_delimited_to_vec();
                            if let Some(writer) = &mut *s_writer.lock().await {
                                // 发送数据到服务端
                                if let Err(e) = writer.write_all(&w_msg).await {
                                    log::error!("发送数据失败:{:?}", e);
                                    break;
                                }
                            }
                        } else{
                            e_tx.send(CommandResult::custom_err("连接断开", SERVER_ERROR_CODE)).await.unwrap();
                            log::warn!("客户端未与服务端连接！");
                        }
                    }
                    _ = shutdown_rx.recv() => {
                        log::debug!("停止 [send to server] Task...");
                        break;
                    }
                }
            }
        });
    }

    // 监听来自服务端的数据
    // 从s_reader读取数据 通过r_tx发送
    async fn listen_read_data(&mut self) {
        let mut shutdown_rx = self.shutdown_tx.subscribe();
        let r_tx = self.r_tx.clone();
        let s_reader = Arc::clone(&self.s_reader);
        let e_tx = self.e_tx.clone();
        log::info!("正在监听来自服务端的数据...");
        // 监听shutdown_rx事件 停止监听服务端的数据
        tokio::spawn(async move {
            if let Some(mut reader) = s_reader.lock().await.take() {
                loop {
                    tokio::select! {
                        res = read_varint(&mut reader) => {
                            match res {
                                Ok(len) => {
                                    let mut buf = vec![0u8; len];
                                    if let Err(e) = reader.read_exact(&mut buf).await {
                                        log::error!("读取数据失败: {:?}", e);
                                        break;
                                    }

                                    match TransferDataMessage::decode(&buf[..]) {
                                        Ok(msg) => {
                                            if let Err(e) = r_tx.send(msg).await {
                                                log::error!("消息发送失败: {}", e);
                                            }
                                        }
                                        Err(e) => log::error!("Protobuf 解析失败: {:?}", e),
                                    }
                                }
                                Err(e) => {
                                    log::error!("读取 Varint 长度前缀失败: {:?}", e);
                                    e_tx.send(CommandResult::custom_err("读取服务端数据异常", SERVER_ERROR_CODE)).await.unwrap();
                                    break;
                                }
                            }
                        }
                        _ = shutdown_rx.recv() => {
                            log::debug!("监听任务收到关闭信号，停止...");
                            break;
                        }
                    }
                }
            } else {
                log::warn!("客户端未与服务器建立连接");
            }
        });
    }

    async fn handler(&mut self) -> Result<()> {
        let auth_message = build_auth_message(self.config.get_password().as_str());
        log::debug!("发送了认证数据:{:?}", auth_message);

        self.s_tx
            .clone()
            .send(auth_message.clone())
            .await
            .with_context(|| format!("发送认证信息失败..."))?;
        let mut shudown_rx = self.shutdown_tx.subscribe();
        let r_rx = Arc::clone(&self.r_rx);
        let config = self.config.clone();
        let s_tx = self.s_tx.clone();
        let local_manager = Arc::clone(&self.local_manager);
        tokio::spawn(async move {
            loop {
                tokio::select! {
                    result = async {
                        let mut r_rx_lock = r_rx.lock().await;
                        r_rx_lock.recv().await
                    } => {
                        if let Some(server_rsp) = result {
                            let server_rsp = Arc::new(server_rsp);
                            let cmd_type = server_rsp.cmd_type();
                            let transfer_meta_data = server_rsp.meta_data.clone();
                            let license_key = match (cmd_type, transfer_meta_data.as_ref()) {
                                (CmdType::AuthErr, _) => None,
                                (_, Some(meta)) => meta.meta_data.get(LICENSE_KEY).cloned(),
                                (_, None) => None,
                            };

                            match cmd_type {
                                CmdType::AuthOk => {
                                    let proxys = config.get_proxies();

                                    for proxy_config in proxys {

                                        let open_server_msg = build_open_server_message(&proxy_config, license_key.clone().unwrap().clone());
                                        s_tx.clone()
                                            .send(open_server_msg)
                                            .await.unwrap();
                                    }
                                }
                                CmdType::AuthErr => {
                                    log::error!("客户端认证失败!");
                                    break;
                                }
                                CmdType::Connect => {
                                    // 创建一个新的 channel 用于与 process 任务通信
                                    let (p_tx, p_rx) = mpsc::channel::<Bytes>(32);
                                    let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                                    let proxy_config = ProxyConfig::from_map(meta_data.clone()).unwrap();
                                    let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                                    let target_addr = format!("{}:{}", proxy_config.host(), proxy_config.port());
                                    {
                                        let local_manager_lock = local_manager.write().await;
                                        local_manager_lock.put_sender(visitor_id.clone(), p_tx).await;
                                    }

                                    local_proxy_handler(
                                        proxy_config,
                                        license_key.unwrap().clone(),
                                        visitor_id.clone(),
                                        p_rx,
                                        target_addr.as_str(),
                                        s_tx.clone(),
                                    )
                                    .await.unwrap();
                                }
                                CmdType::Disconnect => {
                                    let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                                    let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                                    // 移除并关闭对应的 sender，通知 process 内部任务退出
                                    {
                                        let local_manager_lock = local_manager.write().await;

                                        local_manager_lock.remove_sender(&visitor_id).await;
                                    }

                                }
                                CmdType::Transfer => {
                                    let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                                    let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                                    {
                                        let local_manager_lock = local_manager.read().await;
                                        match local_manager_lock.get_sender(&visitor_id).await {
                                            Some(sender) => {
                                                let data = server_rsp.data.clone();
                                                sender.send(Bytes::from(data)).await.unwrap();
                                            }
                                            None => {
                                                log::error!("{:?} 未准备好",visitor_id);
                                            }
                                        }


                                    }

                                }
                                _ => {
                                    log::error!("未知指令类型:{:?}", server_rsp);
                                    break;
                                }
                            }
                        }else{
                            log::warn!("未启动服务端读取监听!");
                        }
                    }
                    _ = shudown_rx.recv() => {
                        log::debug!("停止 [Client Core Handler] Task...");
                        break;
                    }
                }
            }
        });

        Ok(())
    }

    /// 使用广播通知所有任务停止，并关闭所有代理连接
    pub async fn stop(&self) -> Result<()> {
        log::info!("ClientApp 开始停止...");
        // 广播停止信号
        let _ = self.shutdown_tx.send(());

        // 关闭 local_manager 中所有的代理连接
        let visitor_ids = {
            let local_manager = self.local_manager.read().await;
            let senders = local_manager.senders.lock().await;
            senders.keys().cloned().collect::<Vec<String>>()
        };

        {
            let manager = self.local_manager.write().await;
            for visitor_id in visitor_ids {
                manager.remove_sender(&visitor_id).await;
                log::info!("已关闭 visitor_id {} 对应的代理连接", visitor_id);
            }
        }

        Ok(())
    }

    /// 错误处理
    /// 内网穿透程序执行过程中可能会出现错误 根据具体的错误类型 通知给前端 前端进行监听进行响应的处理
    pub async fn listener_err(&self, app: AppHandle) -> Result<()> {
        let app = app.clone();
        // 克隆 Arc，避免引用 `self` 导致生命周期问题
        let e_rx: Arc<Mutex<mpsc::Receiver<CommandResult<()>>>> = Arc::clone(&self.e_rx);
        let mut shutdown_rx = self.shutdown_tx.subscribe();

        tokio::spawn(async move {
            let mut e_rx = e_rx.lock().await; // 锁定并获得接收器的所有权
            loop {
                tokio::select! {
                    // 处理错误事件，收到数据时
                    Some(err_payload) = e_rx.recv() => {
                        log::error!("{:?}",err_payload);
                        if let Err(e) = app.emit_to(EventTarget::webview("app_err_handler"), "app_err_handler", err_payload) {
                            log::error!("Failed to emit error event: {:?}", e);
                        }
                    },
                    // 监听关机信号
                    _ = shutdown_rx.recv() => {
                        log::info!("Shutting down error listener...");
                        break;
                    },
                }
            }
        });

        Ok(())
    }
}

async fn local_proxy_handler(
    proxy_config: ProxyConfig,
    license_key: String,
    visitor_id: String,
    mut rx: mpsc::Receiver<Bytes>,
    target_addr: &str,
    s_tx: mpsc::Sender<TransferDataMessage>,
) -> Result<()> {
    // 建立与目标服务的 TCP 连接，并拆分为读写半部
    let target_connect = match TcpStream::connect(target_addr).await {
        Ok(stream) => stream,
        Err(e) => {
            // 发送 disconnect 消息
            let disconnect_msg = build_disconnect_message(license_key.clone(), visitor_id.clone());
            let _ = s_tx.send(disconnect_msg).await;

            log::error!("连接目标服务失败: {:?}", e);
            return Err(anyhow::anyhow!("连接目标服务失败: {}", e)); // 终止函数
        }
    };
    let (mut target_read, mut target_write) = target_connect.into_split();

    // 先发送连接建立消息给服务端
    let connect_msg = build_connect_message(proxy_config, license_key.clone(), visitor_id.clone());
    s_tx.send(connect_msg).await.unwrap();

    // 任务1：负责从目标服务读取数据，并构造 transfer 消息转发给服务端
    let s_tx_clone = s_tx.clone();
    let visitor_id_clone = visitor_id.clone();

    tokio::spawn(async move {
        let mut buffer = vec![0u8; 1024];
        loop {
            let n = match target_read.read(&mut buffer).await {
                Ok(n) if n == 0 => break, // 连接关闭
                Ok(n) => n,
                Err(e) => {
                    log::error!("从目标连接读取数据失败: {:?}", e);
                    break;
                }
            };
            let data = Bytes::copy_from_slice(&buffer[..n]);
            let transfer_msg = build_transfer_message(
                data.to_vec(),
                visitor_id_clone.clone(),
                license_key.clone(),
            );
            if let Err(e) = s_tx_clone.send(transfer_msg).await {
                log::error!("发送转发消息失败: {:?}", e);
                break;
            }
        }
    });

    // 任务2：负责从上层接收数据并写入目标服务
    tokio::spawn(async move {
        while let Some(data) = rx.recv().await {
            if let Err(e) = target_write.write_all(&data).await {
                log::error!("写入目标连接数据失败: {:?}", e);
                break;
            }
        }
    });

    Ok(())
}

async fn read_varint(reader: &mut OwnedReadHalf) -> Result<usize, std::io::Error> {
    let mut result = 0;
    let mut shift = 0;
    loop {
        let mut buf = [0u8; 1];
        let n = reader.read_exact(&mut buf).await?;
        if n == 0 {
            return Err(std::io::Error::new(
                std::io::ErrorKind::UnexpectedEof,
                "连接已关闭",
            ));
        }
        let byte = buf[0] as usize;
        result |= (byte & 0x7F) << shift;

        if byte & 0x80 == 0 {
            break;
        }
        shift += 7;

        if shift >= 32 {
            return Err(std::io::Error::new(
                std::io::ErrorKind::InvalidData,
                "Varint 过长",
            ));
        }
    }

    Ok(result)
}
