pub mod manager;
use anyhow::Result;
use bytes::BytesMut;
use manager::LocalManager;
use prost::Message;
use std::{error::Error, sync::Arc};
use tauri::{AppHandle, Emitter, EventTarget};
use tokio::{
    io::{AsyncReadExt, AsyncWriteExt},
    net::TcpStream,
    sync::{broadcast, mpsc, Mutex},
};

use crate::{
    common::constants::SERVER_ERROR_CODE,
    core::transfer_message::TransferDataMessage,
    handler::client_handler,
    model::{command::CommandResult, ClientConfig},
};

const FRAME_SIZE: usize = 1024 * 8;

pub struct ClientApp {
    config: ClientConfig,
    local_manager: Arc<Mutex<LocalManager>>,
    shutdown_tx: Arc<broadcast::Sender<()>>,
    e_tx: mpsc::Sender<CommandResult<()>>,
    e_rx: Arc<Mutex<mpsc::Receiver<CommandResult<()>>>>,
}

impl ClientApp {
    pub fn new(config: ClientConfig) -> Self {
        // shutdown 广播
        let (shutdown_tx, _) = broadcast::channel(4);
        let (e_tx, e_rx) = mpsc::channel::<CommandResult<()>>(4);
        ClientApp {
            config,
            local_manager: Arc::new(Mutex::new(LocalManager::new())),
            shutdown_tx: Arc::new(shutdown_tx),
            e_tx,
            e_rx: Arc::new(Mutex::new(e_rx)),
        }
    }

    pub async fn start(&mut self) -> Result<()> {
        // 客户端向服务端写回数据时用到的channel
        let (s_tx, mut s_rx) = mpsc::channel::<TransferDataMessage>(32);
        // 客户端从服务端读取数据时用到的channel
        let (r_tx, r_rx) = mpsc::channel::<TransferDataMessage>(32);
        // 错误通道
        let e_tx1 = self.e_tx.clone();
        let e_tx2 = self.e_tx.clone();
        let mut shutdown_rx1 = self.shutdown_tx.subscribe();
        let mut shutdown_rx2 = self.shutdown_tx.subscribe();
        let shutdown_rx3 = self.shutdown_tx.subscribe();

        let config = self.config.clone();
        let server_host = config.get_server_host();
        let server_port = config.get_server_port();
        let server_addr = format!("{}:{}", server_host, server_port);
        let server_channel = match TcpStream::connect(server_addr).await {
            Ok(channel) => channel,
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
        let (mut s_reader, mut s_writer) = server_channel.into_split();

        // 接收消息 并发送到服务端
        tokio::spawn(async move {
            loop {
                tokio::select! {
                    Some(msg) = s_rx.recv() => {
                        log::info!("接收到消息:{:?}", msg);
                        let mut w_msg = BytesMut::with_capacity(FRAME_SIZE);
                        msg.encode_length_delimited(&mut w_msg).unwrap();
                        s_writer.write_all(&w_msg).await.unwrap();
                    },
                    _ = shutdown_rx1.recv() => {
                        log::info!("停止 [send to server] Task...");
                        break;
                    }
                }
            }
        });

        // 读取数据 并发送到消费者
        tokio::spawn(async move {
            loop {
                let mut buffer = [0; FRAME_SIZE];

                tokio::select! {
                    result = s_reader.read(&mut buffer) => {
                        match result {
                            Ok(n) if n == 0 => {
                                log::info!("服务端连接关闭");
                                e_tx2.send(CommandResult::custom_err("连接断开", SERVER_ERROR_CODE)).await.unwrap();
                                break;
                            },
                            Ok(n) => {
                                let mut read_buf: BytesMut = BytesMut::with_capacity(FRAME_SIZE);
                                read_buf.extend_from_slice(&buffer[..n]);
                                let server_rsp = TransferDataMessage::decode_length_delimited(read_buf).unwrap();
                                r_tx.send(server_rsp).await.unwrap();
                            },
                            Err(e) => {
                                log::error!("从服务端读取数据失败: {:?}", e);
                                break;
                            }

                        }
                    },
                    _ = shutdown_rx2.recv() => {
                        log::info!("停止 [read from server] Task...");
                        // 处理r_tx 断开
                        drop(r_tx);
                        break;
                    }
                }
            }
        });

        // 核心处理业务
        client_handler(
            s_tx.clone(),
            r_rx,
            shutdown_rx3,
            self.local_manager.lock().await.clone(),
            config,
            e_tx1,
        )
        .await
    }

    /// 使用广播通知所有任务停止，并关闭所有代理连接
    pub async fn stop(&self) -> Result<(), Box<dyn Error>> {
        log::info!("ClientApp 开始停止...");
        // 广播停止信号
        let _ = self.shutdown_tx.send(());

        // 关闭 local_manager 中所有的代理连接
        let visitor_ids = {
            let local_manager = self.local_manager.lock().await;
            let senders = local_manager.senders.lock().await;
            senders.keys().cloned().collect::<Vec<String>>()
        };
        for visitor_id in visitor_ids {
            self.local_manager
                .lock()
                .await
                .remove_sender(&visitor_id)
                .await;
            log::info!("已关闭 visitor_id {} 对应的代理连接", visitor_id);
        }

        Ok(())
    }

    ///
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
