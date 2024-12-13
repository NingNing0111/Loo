use std::error::Error;

use tokio::net::TcpStream;

pub struct TcpConnect {
    host: String,
    port: i32,
}

impl TcpConnect {
    pub fn new(host: &str, port: i32) -> Self {
        let host = String::from(host);
        Self { host, port }
    }

    pub async fn connect(&self) -> Result<TcpStream, Box<dyn Error>> {
        let add = format!("{}:{}", &self.host, self.port);
        let tcp_stream = TcpStream::connect(add.as_str()).await?;
        Ok(tcp_stream)
    }
}
