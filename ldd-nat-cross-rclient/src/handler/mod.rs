use std::error::Error;
use std::sync::Arc;

use bytes::BytesMut;
use log::{error, info};
use tokio::task;
use tokio::{io::AsyncReadExt, net::TcpStream};

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
        tcp_stream.flush().await?;
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
    info!("connect_msg:{:?}", connect_msg);
    let connect_msg = helper::encode::encode_message(connect_msg)?;
    server_tcp_stream.write_all(&connect_msg).await?;
    server_tcp_stream.flush().await?;
    Ok(())
}

pub async fn handler_disconnect(
    message: &TransferDataMessage,
    client_manager: &mut ClientManager,
) -> Result<(), Box<dyn Error>> {
    let meta_data = message.meta_data.clone().unwrap().meta_data;
    let visitor_id = meta_data.get(VISITOR_ID).unwrap().to_string();
    client_manager.remove_local_proxy_channel(visitor_id);
    Ok(())
}

pub async fn handler_transfer(
    message: &TransferDataMessage,
    client_manager: &mut ClientManager,
    server_tcp_stream: &mut TcpStream,
) -> Result<(), Box<dyn Error>> {
    let meta_data = message.meta_data.clone().unwrap().meta_data;
    let visitor_id = meta_data.get(VISITOR_ID).unwrap().to_string();
    let local_stream = client_manager
        .get_local_proxy_channel(visitor_id.as_str())
        .ok_or("Visitor not found!")?;

    // 写入 message.data 到 local_stream
    {
        let mut local_stream = local_stream.lock().await; // 加锁以访问流
        local_stream.write_all(&message.data).await?; // 异步写入数据
        local_stream.flush().await?;
    }

    let mut read_buf = BytesMut::with_capacity(1024);
    let mut tmp_buf = [0u8; 1024];
    let local_stream_clone = Arc::clone(&local_stream); // 克隆 Arc
    tokio::spawn(async move {
        let mut local_stream = local_stream_clone.lock().await;
        loop {
            let n = match local_stream.read(&mut tmp_buf).await {
                Ok(b) => b,
                Err(e) => {
                    error!("{}", e);
                    0
                }
            };
            if n == 0 {
                info!("Connection closed by server.");
                break;
            }
            read_buf.extend_from_slice(&tmp_buf[..n]);
            info!("=====>{:?}", String::from_utf8(read_buf.to_vec()));
        }
    });
    Ok(())
}
