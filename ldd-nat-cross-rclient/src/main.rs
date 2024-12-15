use bytes::BytesMut;
use ldd_nat_cross_rclient::common::constants::LICENSE_KEY;
use ldd_nat_cross_rclient::config::client::load_config;
use ldd_nat_cross_rclient::core::cmd_type::{self, CmdType};
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
    let server_hostname = format!(
        "{}/{}",
        client_config.get_server_host(),
        client_config.get_server_port()
    );

    // 认证
    let auth_message = build_auth_message(client_config.get_password());
    let mut message_buf = BytesMut::new();
    auth_message.encode(&mut message_buf)?;

    let length_prefix = encode_varint(message_buf.len() as u32);
    // 创建一个新的缓冲区，将 length_prefix 和 auth_message 合并
    let mut combined_buf = BytesMut::new();
    combined_buf.extend_from_slice(&length_prefix); // 先写入长度前缀
    combined_buf.extend_from_slice(&message_buf); // 再写入消息内容

    // // 将合并后的缓冲区写入 TCP 流
    tcp_stream.write_all(&combined_buf).await?;
    tcp_stream.flush().await?;
    // 接收响应数据
    let mut read_buf = BytesMut::with_capacity(1024);
    let mut tmp_buf = [0u8; 1024];

    loop {
        // 读取数据到缓冲区
        let n = tcp_stream.read(&mut tmp_buf).await?;
        if n == 0 {
            println!("Connection closed by server.");
            break;
        }
        read_buf.extend_from_slice(&tmp_buf[..n]);
        // 尝试解码消息
        if let Some((msg_len, mut remaining)) = decode_varint(&mut read_buf) {
            if remaining.len() >= msg_len as usize {
                let msg_bytes = remaining.split_to(msg_len as usize);
                let received_msg = TransferDataMessage::decode(&msg_bytes[..])?;
                println!("Received message: {:?}", received_msg);
                let cmd_type = received_msg.cmd_type();
                match cmd_type {
                    CmdType::AuthOk => {
                        let license_key = received_msg
                            .meta_data
                            .unwrap()
                            .meta_data
                            .get(LICENSE_KEY)
                            .unwrap()
                            .clone();
                        println!("{}", license_key);
                    }
                    CmdType::AuthErr => {
                        let err_msg = format!("AuthenticationException: Failed to authenticate from server. Server info: {}", server_hostname);
                        panic!("{}", err_msg);
                    }
                    CmdType::Transfer => {}
                    CmdType::Connect => {}
                    CmdType::Disconnect => {}
                    _ => {}
                }
            }
        }
    }

    Ok(())
}
