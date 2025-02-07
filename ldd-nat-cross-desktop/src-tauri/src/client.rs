use std::{collections::HashMap, error::Error, sync::Arc};

use bytes::{Bytes, BytesMut};
use log::info;
use prost::Message;
use tokio::{
    io::{AsyncReadExt, AsyncWriteExt},
    net::TcpStream,
    sync::{broadcast, mpsc, Mutex},
};

use crate::{
    core::transfer_message::TransferDataMessage, handler::client_handler, model::ClientConfig,
};

const FRAME_SIZE: usize = 1024 * 8;

#[derive(Debug, Clone)]
pub struct LocalManager {
    senders: Arc<Mutex<HashMap<String, mpsc::Sender<Bytes>>>>,
}

impl LocalManager {
    pub fn new() -> Self {
        LocalManager {
            senders: Arc::new(Mutex::new(HashMap::new())),
        }
    }

    pub async fn put_sender(&self, visitor_id: String, sender: mpsc::Sender<Bytes>) {
        let mut senders = self.senders.lock().await;
        senders.insert(visitor_id, sender);
    }

    pub async fn get_sender(&self, visitor_id: &str) -> Option<mpsc::Sender<Bytes>> {
        let senders = self.senders.lock().await;
        senders.get(visitor_id).cloned()
    }

    pub async fn remove_sender(&self, visitor_id: &str) {
        let mut senders = self.senders.lock().await;
        if senders.remove(visitor_id).is_some() {
            log::info!("关闭 visitor_id {} 对应的通道", visitor_id);
        }
    }
}

pub struct ClientApp {
    config: ClientConfig,
    local_manager: Mutex<LocalManager>,
    shutdown_tx: broadcast::Sender<()>,
}

impl ClientApp {
    pub fn new(config: ClientConfig) -> Self {
        let (shutdown_tx, _) = broadcast::channel(16);
        ClientApp {
            config,
            local_manager: Mutex::new(LocalManager::new()),
            shutdown_tx,
        }
    }

    pub async fn start(&mut self) -> Result<(), Box<dyn Error>> {
        let config = self.config.clone();
        let server_host = config.get_server_host();
        let server_port = config.get_server_port();
        let server_addr = format!("{}:{}", server_host, server_port);
        let server_channel = match TcpStream::connect(server_addr).await {
            Ok(channel) => channel,
            Err(e) => {
                log::error!("服务端连接失败: {:?}", e);
                return Err(e.into());
            }
        };
        let (mut s_reader, mut s_writer) = server_channel.into_split();
        // 客户端向服务端写回数据时用到的channel
        let (s_tx, mut s_rx) = mpsc::channel::<TransferDataMessage>(32);
        // 客户端从服务端读取数据时用到的channel
        let (r_tx, r_rx) = mpsc::channel::<TransferDataMessage>(32);

        let mut shutdown_rx1 = self.shutdown_tx.subscribe();
        let mut shutdown_rx2 = self.shutdown_tx.subscribe();
        let shutdown_rx3 = self.shutdown_tx.subscribe();

        // 接收消息 并发送到服务端
        tokio::spawn(async move {
            loop {
                tokio::select! {
                    Some(msg) = s_rx.recv() => {
                        log::info!("send to server: {:?}", msg);
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
                                break;
                            },
                            Ok(n) => {
                                let mut read_buf: BytesMut = BytesMut::with_capacity(FRAME_SIZE);
                                read_buf.extend_from_slice(&buffer[..n]);
                                let server_rsp = TransferDataMessage::decode_length_delimited(read_buf).unwrap();
                                info!("response from server: {:?}", server_rsp);
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
        let _ = client_handler(
            s_tx.clone(),
            r_rx,
            shutdown_rx3,
            self.local_manager.lock().await.clone(),
            config,
        )
        .await;

        Ok(())
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
}
