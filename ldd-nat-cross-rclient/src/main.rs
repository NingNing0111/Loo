use bytes::{Bytes, BytesMut};
use ldd_nat_cross_rclient::{
    common::constants::{LICENSE_KEY, VISITOR_ID},
    config::{arg::get_args, client::get_config, log::init_log},
    core::{cmd_type::CmdType, transfer_message::TransferDataMessage},
    helper::message::{
        build_auth_message, build_connect_message, build_disconnect_message,
        build_open_server_message, build_transfer_message,
    },
    model::{protocol::ProtocolEnum, proxy::ProxyConfig},
};
use log::info;
use prost::Message;
use std::{collections::HashMap, error::Error};
use tokio::{io::AsyncReadExt, sync::mpsc};
use tokio::{io::AsyncWriteExt, sync::Mutex};
use tokio::{net::TcpStream, sync::mpsc::Sender};

#[derive(Debug)]
struct LocalManager {
    senders: Mutex<HashMap<String, mpsc::Sender<Bytes>>>,
}

impl LocalManager {
    pub fn new() -> Self {
        LocalManager {
            senders: Mutex::new(HashMap::new()),
        }
    }
}

async fn put_sender(
    local_manager: &Mutex<LocalManager>,
    visitor_id: String,
    sender: Sender<Bytes>,
) {
    // 锁定外层的 LocalManager
    let manager = local_manager.lock().await;
    // 锁定 LocalManager 内部的 senders 哈希表
    let mut senders = manager.senders.lock().await;
    // 插入新的 sender
    senders.insert(visitor_id, sender);
}

async fn get_sender(
    local_manager: &Mutex<LocalManager>,
    visitor_id: &str,
) -> Option<Sender<Bytes>> {
    // 锁定外层的 LocalManager
    let manager = local_manager.lock().await;
    // 锁定内部的 senders 哈希表
    let senders = manager.senders.lock().await;
    // 查找对应的 sender 并克隆返回
    senders.get(visitor_id).cloned()
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let args = get_args();
    let config_file_path = args.get_config_path();
    let all_config = get_config(config_file_path).expect("parse config file fail!");
    let log_config = all_config.get_log_config();
    init_log(log_config).expect("init log config fail!");

    let client_config = all_config.get_client_config();
    let server_addr = format!(
        "{}:{}",
        client_config.get_server_host(),
        client_config.get_server_port()
    );

    // 建立连接
    let tcp_connect = TcpStream::connect(server_addr).await?;
    let (mut reader, mut writer) = tcp_connect.into_split();
    // 客户端向服务端写回数据时用到的channel
    let (s_tx, mut s_rx) = mpsc::channel::<TransferDataMessage>(32);
    // 客户端从服务端读取数据时用到的channel
    let (r_tx, mut r_rx) = mpsc::channel::<TransferDataMessage>(32);
    let local_manager = Mutex::new(LocalManager::new());

    // 接收消息 并发送到服务端
    tokio::spawn(async move {
        while let Some(msg) = s_rx.recv().await {
            info!("send to server: {:?}", msg);
            let mut w_msg = BytesMut::with_capacity(1024 * 8);
            msg.encode_length_delimited(&mut w_msg).unwrap();
            writer.write_all(&w_msg).await.unwrap();
        }
    });

    // 读取数据 并发送到消费者
    tokio::spawn(async move {
        loop {
            let mut buffer = [0; 1024 * 8];
            let n = reader.read(&mut buffer).await.unwrap();
            let mut read_buf: BytesMut = BytesMut::with_capacity(1024 * 8);
            read_buf.extend_from_slice(&buffer[..n]);
            let server_rsp = TransferDataMessage::decode_length_delimited(read_buf).unwrap();
            info!("response from server: {:?}", server_rsp);
            r_tx.send(server_rsp).await.unwrap();
        }
    });

    let auth_message = build_auth_message(client_config.get_password());

    s_tx.clone().send(auth_message).await.unwrap();

    // 消费 生产者生产的数据
    while let Some(server_rsp) = r_rx.recv().await {
        let license_key = server_rsp
            .meta_data
            .as_ref()
            .unwrap()
            .meta_data
            .get(LICENSE_KEY)
            .unwrap()
            .clone();
        let cmd_type = server_rsp.cmd_type();
        match cmd_type {
            CmdType::AuthOk => {
                let proxy_config =
                    ProxyConfig::new(String::from("localhost"), 3306, 9011, ProtocolEnum::TCP);
                let open_server_msg = build_open_server_message(&proxy_config, license_key.clone());

                s_tx.clone()
                    .send(open_server_msg)
                    .await
                    .expect("send fail!");
            }
            CmdType::AuthErr => {
                break;
            }
            CmdType::Connect => {
                // 创建一个新的 channel 用于与 process 任务通信
                let (p_tx, p_rx) = mpsc::channel::<Bytes>(32);

                let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                let proxy_config = ProxyConfig::from_map(meta_data.clone()).unwrap();
                let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                let target_addr = format!("{}:{}", proxy_config.host(), proxy_config.port());
                put_sender(&local_manager, visitor_id.clone(), p_tx).await;

                process(
                    proxy_config,
                    license_key.clone(),
                    visitor_id.clone(),
                    p_rx,
                    target_addr.as_str(),
                    s_tx.clone(),
                )
                .await
                .expect("process fail!");
            }
            CmdType::Disconnect => {
                let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                log::error!("收到 disconnect 消息，visitor_id: {}", visitor_id);
                // 移除并关闭对应的 sender，通知 process 内部任务退出
                remove_sender(&local_manager, &visitor_id).await;
            }
            CmdType::Transfer => {
                let meta_data = server_rsp.meta_data.as_ref().unwrap().meta_data.clone();
                let visitor_id = meta_data.get(VISITOR_ID).unwrap().clone();
                let sender = get_sender(&local_manager, &visitor_id).await.unwrap();
                let data = server_rsp.data.clone();
                sender.send(Bytes::from(data)).await.unwrap();
            }
            _ => {
                break;
            }
        }
    }

    Ok(())
}

async fn process(
    proxy_config: ProxyConfig,
    license_key: String,
    visitor_id: String,
    mut rx: mpsc::Receiver<Bytes>,
    target_addr: &str,
    s_tx: mpsc::Sender<TransferDataMessage>,
) -> Result<(), Box<dyn std::error::Error>> {
    // 建立与目标服务的 TCP 连接，并拆分为读写半部
    let target_connect = match TcpStream::connect(target_addr).await {
        Ok(stream) => stream,
        Err(e) => {
            log::error!("连接目标服务失败: {:?}", e);
            // 发送disconnect
            let disconnect_msg = build_disconnect_message(license_key.clone(), visitor_id.clone());
            s_tx.send(disconnect_msg).await?;
            return Err(e.into());
        }
    };
    let (mut target_read, mut target_write) = target_connect.into_split();

    // 先发送连接建立消息给服务端
    let connect_msg = build_connect_message(proxy_config, license_key.clone(), visitor_id.clone());
    s_tx.send(connect_msg).await?;

    // 任务1：负责从目标服务读取数据，并构造 transfer 消息转发给服务端
    let s_tx_clone = s_tx.clone();
    let visitor_id_clone = visitor_id.clone();
    tokio::spawn(async move {
        let mut buffer = [0u8; 1024 * 8];
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

async fn remove_sender(local_manager: &Mutex<LocalManager>, visitor_id: &str) {
    // 锁定外层的 LocalManager
    let manager = local_manager.lock().await;
    // 锁定内部的 senders 哈希表
    let mut senders = manager.senders.lock().await;
    if senders.remove(visitor_id).is_some() {
        log::info!("关闭 visitor_id {} 对应的通道", visitor_id);
    }
}
