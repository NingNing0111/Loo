pub mod client_handler;

// use std::error::Error;
// use std::sync::Arc;

// use bytes::BytesMut;
// use log::{error, info};
// use tokio::task;
// use tokio::{io::AsyncReadExt, net::TcpStream};

// use crate::{
//     client::client::ClientManager,
//     common::constants::{LICENSE_KEY, VISITOR_ID},
//     config::client::ClientConfig,
//     core::transfer_message::TransferDataMessage,
//     helper::{self, message::build_connect_message},
//     model::proxy::ProxyConfig,
// };
// use tokio::io::AsyncWriteExt;

// pub async fn handler_ok(
//     tcp_stream: &mut TcpStream,
//     license_key: &str,
//     client_config: &ClientConfig,
// ) -> Result<(), Box<dyn Error>> {
//     for proxy_config in client_config.get_proxy().iter() {
//         let open_server_msg =
//             helper::message::build_open_server_message(proxy_config, license_key.to_string());

//         let open_server_message = helper::encode::encode_message(open_server_msg)?;

//         tcp_stream.write_all(&open_server_message).await?;
//         tcp_stream.flush().await?;
//     }
//     Ok(())
// }

// pub async fn handler_connect(
//     server_tcp_stream: &mut TcpStream,
//     message: &TransferDataMessage,
//     client_manager: &mut ClientManager,
// ) -> Result<(), Box<dyn Error>> {
//     Ok(())
// }

// pub async fn handler_disconnect(
//     message: &TransferDataMessage,
//     client_manager: &mut ClientManager,
// ) -> Result<(), Box<dyn Error>> {
//     Ok(())
// }

// pub async fn handler_transfer(
//     message: &TransferDataMessage,
//     client_manager: &mut ClientManager,
//     server_tcp_stream: &mut TcpStream,
// ) -> Result<(), Box<dyn Error>> {
//     Ok(())
// }
