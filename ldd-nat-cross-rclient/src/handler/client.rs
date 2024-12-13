use std::error::Error;

use tokio::{io::AsyncReadExt, net::TcpStream};

use crate::config::client::ClientConfig;
pub struct ClientHandler {
    config: ClientConfig,
    target: TcpStream,
}

impl ClientHandler {}
