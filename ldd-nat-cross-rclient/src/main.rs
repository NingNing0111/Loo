use bytes::buf;
use ldd_nat_cross_rclient::config::arg::load_args;
use ldd_nat_cross_rclient::config::client::load_config;
use ldd_nat_cross_rclient::helper;
use ldd_nat_cross_rclient::net::tcp_connect::TcpConnect;
use prost::Message;
use std::error::Error;
use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio_util::codec::{Encoder, LengthDelimitedCodec};

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
    let auth_message = helper::build_auth_message(client_config.get_password());
    // 发送
    // 将消息编码为字节
    let mut buf = Vec::new();
    auth_message
        .encode(&mut buf)
        .expect("Failed to encode message");

    // 发送数据
    tcp_stream
        .write_all(&buf)
        .await
        .expect("Failed to send message");
    tcp_stream.flush().await.expect("Failed to flush data");
    loop {
        let mut buf = vec![0; 1024];
        let n = tcp_stream.read(&mut buf).await?;
        if n == 0 {
            println!("client disconnected...");
            break;
        }
    }

    Ok(())
}
