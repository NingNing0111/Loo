use std::error::Error;
use std::sync::Arc;

use tokio::io::AsyncWriteExt;
use tokio::net::TcpStream;
use tokio::sync::Mutex;

#[derive(Debug, Clone)]
pub struct TcpConnect {
    stream: Arc<Mutex<TcpStream>>,
}

impl TcpConnect {
    pub async fn connect(host: &str, port: i32) -> Result<Self, Box<dyn Error>> {
        let stream = TcpStream::connect(format!("{}:{}", host, port)).await?;
        Ok(Self {
            stream: Arc::new(Mutex::new(stream)),
        })
    }

    pub fn stream(&self) -> Arc<Mutex<TcpStream>> {
        self.stream.clone()
    }

    pub async fn shutdown(&self) -> Result<(), Box<dyn Error>> {
        self.stream.lock().await.shutdown().await?;
        Ok(())
    }
}
