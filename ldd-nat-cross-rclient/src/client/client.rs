use log::info;
use std::{collections::HashMap, sync::Arc};
use tokio::net::TcpStream;
use tokio::signal::unix::Signal;
use tokio::sync::watch;
use tokio::sync::RwLock;

pub struct Client {
    // 客户端状态信号 判断是否启动和停止
    signal: Signal,
    shutdown: watch::Receiver<bool>,
}

impl Client {
    pub fn new() -> std::io::Result<Self> {
        let (tx, rx) = watch::channel(false);
        Ok(Self {
            signal: tokio::signal::unix::signal(tokio::signal::unix::SignalKind::interrupt())?,
            shutdown: rx,
        })
    }

    /// 启动客户端
    pub fn start(&self) {}

    /// 停止客户端
    pub async fn stop(&mut self) {
        self.signal.recv().await;
        info!("client stop");
    }
}

#[derive(Clone, Default)]
pub struct ClientManager {
    inner: Arc<RwLock<HashMap<String, Arc<TcpStream>>>>, // 使用 Arc<TcpStream>
}

impl ClientManager {
    pub fn new() -> Self {
        Self {
            inner: Arc::new(RwLock::new(HashMap::new())),
        }
    }

    pub async fn set_local_proxy_channel(&self, visitor: String, tcp_stream: TcpStream) {
        let mut map = self.inner.write().await;
        map.insert(visitor, Arc::new(tcp_stream));
    }

    pub async fn get_local_proxy_channel(&self, visitor: &str) -> Option<Arc<TcpStream>> {
        let map = self.inner.read().await; // 使用读锁，因为是获取数据
        map.get(visitor).cloned() // 返回克隆的 Arc<TcpStream>，避免移动所有权
    }

    pub async fn remove_local_proxy_channel(&self, visitor: &str) {
        let mut map = self.inner.write().await;
        map.remove(visitor); // 直接移除
    }
}
