use bytes::BytesMut;
use log::{error, info};
use std::error::Error;
use std::{collections::HashMap, sync::Arc};
use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio::select;
use tokio::sync::RwLock;

use crate::common::constants;
use crate::config::client::ClientConfig;
use crate::core::cmd_type::CmdType;
use crate::core::transfer_message::TransferDataMessage;
use crate::helper::decode::decode_message;
use crate::helper::encode::encode_message;
use crate::helper::message::{
    build_auth_message, build_connect_message, build_disconnect_message, build_open_server_message,
    build_transfer_message,
};
use crate::model::proxy::ProxyConfig;
use crate::net::tcp_connect::TcpConnect;

pub struct Client {
    // 客户端状态信号 判断是否启动和停止
    shutdown_signal: RwLock<bool>,
    client_config: ClientConfig,
    client_manager: ClientManager,
    license_key: String,
    server_connect: Option<TcpConnect>,
}

impl Client {
    pub fn new(client_config: ClientConfig) -> Self {
        Self {
            shutdown_signal: RwLock::new(false),
            client_config,
            client_manager: ClientManager::new(),
            license_key: String::new(),
            server_connect: None,
        }
    }

    /// 启动客户端
    pub async fn start(&mut self) -> Result<(), Box<dyn Error>> {
        // 连接到服务器
        self.server_connect = Some(
            TcpConnect::connect(
                self.client_config.get_server_host(),
                self.client_config.get_server_port(),
            )
            .await?,
        );

        // 发送认证消息
        let auth_message = build_auth_message(self.client_config.get_password());
        self.write_message(auth_message).await?;

        loop {
            // stop client
            if *self.shutdown_signal.read().await {
                self.server_connect.as_ref().unwrap().shutdown().await?;
                info!("client stop successful!");
                break;
            }

            let received_msg = self.read_message().await?;
            info!("received message: {:?}", received_msg);
            let cmd_type = received_msg.cmd_type();
            match cmd_type {
                CmdType::AuthErr => {
                    error!("client auth error!");
                    self.stop().await;
                    break;
                }
                CmdType::AuthOk => {
                    self.handle_auth_ok(received_msg).await?;
                }
                CmdType::Connect => {
                    self.handle_connect(received_msg).await?;
                }
                CmdType::Transfer => {
                    self.handle_transfer(received_msg).await?;
                }
                CmdType::Disconnect => {
                    self.handle_disconnect(received_msg).await?;
                }
                _ => {}
            }
        }

        Ok(())
    }

    /// 停止客户端
    pub async fn stop(&mut self) {
        *self.shutdown_signal.try_write().unwrap() = true;
    }

    /// 读取server端消息
    async fn read_message(&self) -> Result<TransferDataMessage, Box<dyn Error>> {
        let mut tmp_buf = [0u8; 1024 * 4];
        // 读取数据到缓冲区
        let mut stream = self.server_connect.as_ref().unwrap().stream().lock().await;

        let n = stream.read(&mut tmp_buf).await?;
        if n == 0 {
            info!("Connection closed by server.");
            return Err(Box::new(std::io::Error::new(
                std::io::ErrorKind::ConnectionReset,
                "Connection closed by server.",
            )));
        }
        let mut read_buf = BytesMut::with_capacity(1024 * 4);
        read_buf.extend_from_slice(&tmp_buf[..n]);
        let received_msg = decode_message(&mut read_buf).expect("decode_message fail!");
        Ok(received_msg)
    }

    /// 向server端写入msg
    async fn write_message(&self, message: TransferDataMessage) -> Result<(), Box<dyn Error>> {
        let encoded_message = encode_message(message.clone())?;
        {
            let mut stream = self.server_connect.as_ref().unwrap().stream().lock().await;
            stream.write_all(&encoded_message).await?;
            stream.flush().await?;
        } // 锁在这里显式释放
        Ok(())
    }

    /// 处理认证通过
    async fn handle_auth_ok(
        &mut self,
        received_msg: TransferDataMessage,
    ) -> Result<(), Box<dyn Error>> {
        info!(
            "connected to server successful! server address:{}/{}",
            self.client_config.get_server_host(),
            self.client_config.get_server_port()
        );
        let meta = received_msg.meta_data.as_ref().unwrap();
        let license_key = meta.meta_data.get(constants::LICENSE_KEY).unwrap().as_str();
        self.license_key = license_key.to_string();

        let proxy_config_list = self.client_config.get_proxy();
        for proxy_config in proxy_config_list {
            let open_server_msg = build_open_server_message(proxy_config, license_key.to_string());
            self.write_message(open_server_msg)
                .await
                .expect("write_message fail!");
        }
        Ok(())
    }

    /// 处理连接
    /// 实现：received_msg 是Server发送过来的 携带ProxyConfig 根据ProxyConfig 创建本地连接 并将连接通道已Map的形式记录，Map：visitorId->TcpConnect
    /// 后续操作中，client会根据visitorId 获取对应的TcpConnect 写入数据 并将响应到的数据 写回给server端
    async fn handle_connect(
        &mut self,
        received_msg: TransferDataMessage,
    ) -> Result<(), Box<dyn Error>> {
        let meta = received_msg.meta_data.as_ref().unwrap();
        let visitor_id = meta.meta_data.get(constants::VISITOR_ID).unwrap();
        let proxy_config = ProxyConfig::from_map(meta.meta_data.clone()).unwrap();
        let local_tcp_connect =
            TcpConnect::connect(proxy_config.host(), proxy_config.port()).await?;
        self.client_manager
            .set_local_proxy_channel(visitor_id.to_string(), local_tcp_connect)
            .await;
        let connect_msg = build_connect_message(
            proxy_config,
            self.license_key.clone(),
            visitor_id.to_string(),
        );
        self.write_message(connect_msg).await?;

        Ok(())
    }

    /// 处理数据传输
    /// 实现：received_msg 是Server发送过来的 携带bytes数据和visitorId
    /// 根据visitorId 获取对应的TcpConnect 写入数据 并将响应到的数据 写回给server端
    async fn handle_transfer(
        &mut self,
        received_msg: TransferDataMessage,
    ) -> Result<(), Box<dyn Error>> {
        let meta = received_msg.meta_data.as_ref().unwrap();
        let visitor_id = meta.meta_data.get(constants::VISITOR_ID).unwrap();
        let local_tcp_connect = self
            .client_manager
            .get_local_proxy_channel(visitor_id)
            .await;
        match local_tcp_connect {
            Some(local_tcp_connect) => {
                let mut stream = local_tcp_connect.stream().lock().await;

                select! {
                    result = async {
                        stream.write_all(&received_msg.data).await?;
                        stream.flush().await?;
                        Ok::<_, Box<dyn Error>>(())
                    } => result?,

                    _ = tokio::time::sleep(std::time::Duration::from_secs(5)) => {
                        return Err("Operation timed out".into());
                    }
                }

                let mut read_buf = BytesMut::with_capacity(1024 * 4);
                stream.read_buf(&mut read_buf).await?;

                let transfer_data_message =
                    build_transfer_message(read_buf.to_vec(), visitor_id.to_string());
                self.write_message(transfer_data_message).await?;
            }
            None => {
                let disconnect_msg =
                    build_disconnect_message(self.license_key.clone(), visitor_id.to_string());
                self.write_message(disconnect_msg).await?;
            }
        }
        Ok(())
    }

    /// 处理断开连接
    async fn handle_disconnect(
        &mut self,
        received_msg: TransferDataMessage,
    ) -> Result<(), Box<dyn Error>> {
        let meta = received_msg.meta_data.as_ref().unwrap();
        let visitor_id = meta.meta_data.get(constants::VISITOR_ID).unwrap();
        self.client_manager
            .remove_local_proxy_channel(visitor_id)
            .await;
        Ok(())
    }
}

#[derive(Clone, Default)]
pub struct ClientManager {
    inner: Arc<RwLock<HashMap<String, Arc<TcpConnect>>>>, // 使用 Arc<RwLock> 包装 HashMap
}

impl ClientManager {
    pub fn new() -> Self {
        Self {
            inner: Arc::new(RwLock::new(HashMap::new())),
        }
    }

    pub async fn set_local_proxy_channel(&self, visitor: String, tcp_connect: TcpConnect) {
        let mut map = self.inner.write().await; // 获取写锁
        map.insert(visitor, Arc::new(tcp_connect));
    } // 写锁在这里自动释放 (当 map 离开作用域时)

    pub async fn get_local_proxy_channel(&self, visitor: &str) -> Option<Arc<TcpConnect>> {
        let map = self.inner.read().await; // 获取读锁
        map.get(visitor).cloned() // 克隆 Arc<TcpConnect>
    } // 读锁在这里自动释放 (当 map 离开作用域时)

    pub async fn remove_local_proxy_channel(&self, visitor: &str) {
        let mut map = self.inner.write().await;
        map.remove(visitor); // 直接移除
    }
}
