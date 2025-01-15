use std::error::Error;
use bytes::{ BytesMut};
use log::error;
use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio::net::TcpStream;

#[derive(Debug)]
pub struct TcpConnect {
    stream: TcpStream,
    buffer: BytesMut,
}

impl TcpConnect {
    /// 创建新的 TcpConnect 实例
    pub async fn new(host: &str, port: i32) -> Option<TcpConnect> {
        let addr = format!("{}:{}", host, port);
        let stream = match TcpStream::connect(addr).await {
            Ok(s) => s,
            Err(e) => {
                error!("Failed to connect to {}:{}. Error: {}", host, port, e);
                return None;
            }
        };

        Some(TcpConnect {
            stream,
            buffer: BytesMut::with_capacity(4096), // 设置初始缓冲区大小
        })
    }

    /// 关闭连接
    pub async fn close(&mut self) -> Result<(), Box<dyn Error>> {
        self.stream.shutdown().await?;
        Ok(())
    }

    /// 向连接写入数据
    pub async fn write(&mut self, data: &[u8]) -> Result<(), Box<dyn Error>> {
        self.stream.write_all(data).await?;
        Ok(())
    }

    /// 从连接读取数据
    pub async fn read(&mut self) -> Result<usize, Box<dyn Error>> {
        self.buffer.clear();
        let n = self.stream.read_buf(&mut self.buffer).await?;
        Ok(n)
    }

    /// 获取当前缓冲区的数据
    pub fn get_buffer(&self) -> &[u8] {
        &self.buffer
    }
}
