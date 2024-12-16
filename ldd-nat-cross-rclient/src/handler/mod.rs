use std::error::Error;

use serde::ser;
use tokio::net::TcpStream;

use crate::{
    client::client::ClientManager,
    common::constants::{LICENSE_KEY, VISITOR_ID},
    config::client::ClientConfig,
    core::transfer_message::TransferDataMessage,
    helper::{self, message::build_connect_message},
    model::proxy::ProxyConfig,
};
use tokio::io::AsyncWriteExt;

pub async fn handler_ok(
    tcp_stream: &mut TcpStream,
    license_key: &str,
    client_config: &ClientConfig,
) -> Result<(), Box<dyn Error>> {
    for proxy_config in client_config.get_proxy().clone() {
        let open_server_msg =
            helper::message::build_open_server_message(proxy_config, license_key.to_string());
        let open_server_message = helper::encode::encode_message(open_server_msg)?;
        tcp_stream.write_all(&open_server_message).await?;
    }

    Ok(())
}

pub async fn handler_connect(
    server_tcp_stream: &mut TcpStream,
    message: &TransferDataMessage,
    client_manager: &mut ClientManager,
) -> Result<(), Box<dyn Error>> {
    let meta_data = message.meta_data.clone().unwrap().meta_data;
    let visitor_id = meta_data.get(VISITOR_ID).unwrap().to_string();
    let license_key = meta_data.get(LICENSE_KEY).unwrap().to_string();
    let proxy_config = ProxyConfig::from_map(&meta_data).unwrap();
    let addr = format!("{}:{}", proxy_config.host(), proxy_config.open_port());
    let local_tcp_stream = TcpStream::connect(addr).await?;
    client_manager.set_local_proxy_channel(visitor_id.clone(), local_tcp_stream);
    let connect_msg = build_connect_message(proxy_config, license_key, visitor_id);
    let connect_msg = helper::encode::encode_message(connect_msg)?;
    server_tcp_stream.write_all(&connect_msg).await?;
    Ok(())
}
