use bytes::BytesMut;
use ldd_nat_cross_rclient::config::client::load_config;
use ldd_nat_cross_rclient::core::transfer_message::TransferDataMessage;
use ldd_nat_cross_rclient::helper::{build_auth_message, decode_varint};
use ldd_nat_cross_rclient::net::tcp_connect::TcpConnect;
use ldd_nat_cross_rclient::{config::arg::load_args, helper::encode_varint};
use prost::Message;
use std::error::Error;
use tokio::io::{AsyncReadExt, AsyncWriteExt};

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let args = load_args();
    let config_file_path = args.get_config_path();
    let client_config = load_config(config_file_path)?;
    let mut tcp_stream = TcpConnect::new(
        client_config.get_server_host(),
        client_config.get_server_port(),
    )
    .connect()
    .await?;
    // 认证
    let auth_message = build_auth_message(client_config.get_password());
    let mut message_buf = BytesMut::new();
    auth_message.encode(&mut message_buf)?;

    let length_prefix = encode_varint(message_buf.len() as u32);
    tcp_stream.write_all(&length_prefix).await?;
    tcp_stream.write_all(&message_buf).await?;
    // tcp_stream.flush().await?;

    let mut response_buf = BytesMut::with_capacity(1024);
    loop {
        let n = tcp_stream.read(&mut response_buf).await?;
        if n == 0 {
            println!("client disconnected...");
            break;
        }

        // 打印响应内容
        // 检查缓冲区中是否有完整的消息
        // while let Some((message_len, mut remaining)) = decode_varint(&mut response_buf) {
        //     if remaining.len() < message_len as usize {
        //         // 等待更多数据
        //         break;
        //     }

        //     // 提取完整消息
        //     let full_message = remaining.split_to(message_len as usize);

        //     // 解码消息
        //     match TransferDataMessage::decode(full_message) {
        //         Ok(response) => {
        //             println!("Received response: {:?}", response);
        //         }
        //         Err(e) => {
        //             println!("Failed to decode response: {:?}", e);
        //         }
        //     }
        // }
    }

    Ok(())
}
