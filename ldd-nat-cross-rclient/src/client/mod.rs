pub mod manager;
use bytes::BytesMut;
use manager::LocalManager;
use prost::Message;
use std::{error::Error, sync::Arc};
use tokio::{
    io::{AsyncReadExt, AsyncWriteExt},
    net::TcpStream,
    sync::{broadcast, mpsc, Mutex},
};

use crate::{
    config::client::ClientConfig, core::transfer_message::TransferDataMessage,
    handler::client_handler,
};

const FRAME_SIZE: usize = 1024 * 8;

pub struct ClientApp {
    config: ClientConfig,
    local_manager: Arc<Mutex<LocalManager>>,
    shutdown_tx: Arc<broadcast::Sender<()>>,
}

impl ClientApp {
    pub fn new(config: ClientConfig) -> Self {
        // shutdown 广播
        let (shutdown_tx, _) = broadcast::channel(4);
        ClientApp {
            config,
            local_manager: Arc::new(Mutex::new(LocalManager::new())),
            shutdown_tx: Arc::new(shutdown_tx),
        }
    }

    pub async fn start(&mut self) -> Result<(), Box<dyn Error>> {
        // 客户端向服务端写回数据时用到的channel
        let (s_tx, mut s_rx) = mpsc::channel::<TransferDataMessage>(32);
        // 客户端从服务端读取数据时用到的channel
        let (r_tx, r_rx) = mpsc::channel::<TransferDataMessage>(32);
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
                log::info!("无法与服务端建立连接:{:?}", e);
                return Err(e.into());
            }
        };
        let (mut s_reader, mut s_writer) = server_channel.into_split();

        // 接收消息 并发送到服务端
        tokio::spawn(async move {
            loop {
                tokio::select! {
                    Some(msg) = s_rx.recv() => {
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
        )
        .await
        .unwrap();

        loop {
            if let Ok(_) = self.shutdown_tx.subscribe().recv().await {
                break;
            }
        }
        Ok(())
    }
}
