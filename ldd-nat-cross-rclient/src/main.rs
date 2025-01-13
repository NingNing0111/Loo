use bytes::BytesMut;
use ldd_nat_cross_rclient::client::client::ClientManager;
use ldd_nat_cross_rclient::common::constants::LICENSE_KEY;
use ldd_nat_cross_rclient::config::arg::get_args;
use ldd_nat_cross_rclient::config::client::{get_config, ClientConfig};
use ldd_nat_cross_rclient::config::log::init_log;
use ldd_nat_cross_rclient::core::cmd_type::CmdType;
use ldd_nat_cross_rclient::helper;
use ldd_nat_cross_rclient::net::tcp_connect::TcpConnect;
use log::{error, info};
use std::error::Error;
use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio::net::TcpStream;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let args = get_args();
    let config_file_path = args.get_config_path();
    let all_config = get_config(config_file_path).expect("parse config file fail!");
    let client_config = all_config.get_client_config();
    let log_config = all_config.get_log_config();
    init_log(log_config).expect("init log config fail!");

    let server_hostname = format!(
        "{}:{}",
        client_config.get_server_host(),
        client_config.get_server_port()
    );
    let mut tcp_stream = TcpConnect::new(
        client_config.get_server_host(),
        client_config.get_server_port(),
    )
    .connect()
    .await?;

    // 发送 认证消息
    send_auth_message(&mut tcp_stream, &client_config)
        .await
        .expect("send auth message error!");

    // 接收响应数据

    let mut client_manager = ClientManager::new();

    loop {
        let mut tmp_buf = [0u8; 1024 * 4];
        // 读取数据到缓冲区
        let n = tcp_stream.read(&mut tmp_buf).await?;
        if n == 0 {
            info!("Connection closed by server.");
            break;
        }

        let mut read_buf = BytesMut::with_capacity(1024 * 4);
        read_buf.extend_from_slice(&tmp_buf[..n]);
        let received_msg =
            helper::decode::decode_message(&mut read_buf).expect("decode_message fail!");
        let cmd_type = received_msg.cmd_type();
        match cmd_type {
            CmdType::AuthOk => {
                info!(
                    "connected to server successful! server address:{}",
                    server_hostname
                );
                let meta_data = received_msg.clone().meta_data.unwrap();
                let license_key = meta_data.meta_data.get(LICENSE_KEY).unwrap().as_str();
            }
            CmdType::AuthErr => {
                error!(
                    "AuthenticationException: Failed to authenticate from server. Server info: {}",
                    server_hostname
                );
                break;
            }
            CmdType::Transfer => {}
            CmdType::Connect => {}
            CmdType::Disconnect => {}
            _ => {
                error!("unknow message:{:?}", received_msg);
                break;
            }
        }
    }

    Ok(())
}

/// 发送认证消息
///
/// # 参数
/// - `tcp_stream`: TCP连接通道;
/// - `client_config`: 客户端配置，主要是通过这个获取 password;
async fn send_auth_message(
    tcp_stream: &mut TcpStream,
    client_config: &ClientConfig,
) -> Result<(), Box<dyn Error>> {
    let auth_message = helper::message::build_auth_message(client_config.get_password());
    let auth_message = helper::encode::encode_message(auth_message)?;
    tcp_stream.write_all(&auth_message).await?;
    tcp_stream.flush().await?;
    Ok(())
}
