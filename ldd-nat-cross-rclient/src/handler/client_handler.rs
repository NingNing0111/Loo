use std::error::Error;

use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio::net::TcpStream;

use crate::client::client::ClientManager;
use crate::common::constants::VISITOR_ID;
use crate::core::transfer_message::TransferDataMessage;
use crate::net::tcp_connect::TcpConnect;
use crate::{config::client::ClientConfig, helper};

pub struct ClientHandler {
    client_config: ClientConfig,
    client_manager: ClientManager,
}

impl ClientHandler {
    pub fn new(client_config: ClientConfig, client_manager: ClientManager) -> Self {
        Self {
            client_config,
            client_manager,
        }
    }

    /// 处理认证没问题的情况
    ///
    pub async fn handler_ok(
        &self,
        license_key: &str,
        server_stream: &mut TcpStream,
    ) -> Result<(), Box<dyn Error>> {
        for proxy_config in self.client_config.get_proxy().iter() {
            let open_server_msg =
                helper::message::build_open_server_message(proxy_config, license_key.to_string());
            let open_server_message = helper::encode::encode_message(open_server_msg)?;
            server_stream.write_all(&open_server_message).await?;
        }
        Ok(())
    }

    pub async fn handler_connect(
        &self,
        message: &TransferDataMessage,
    ) -> Result<(), Box<dyn Error>> {
        let meta = message.meta_data.clone().unwrap().meta_data;
        let visitor_id = meta.get(VISITOR_ID).unwrap().to_string();
        let tcp_stream = TcpConnect::new(
            self.client_config.get_server_host(),
            self.client_config.get_server_port(),
        )
        .connect()
        .await?;
        self.client_manager
            .set_local_proxy_channel(visitor_id, tcp_stream)
            .await;
        Ok(())
    }
}
