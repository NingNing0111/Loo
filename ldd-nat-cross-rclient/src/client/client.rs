use std::{collections::HashMap, sync::Arc};

use tokio::{net::TcpStream, sync::Mutex};

pub struct Client;

pub struct ClientManager {
    proxy_channel: HashMap<String, Arc<Mutex<TcpStream>>>,
}

impl ClientManager {
    pub fn new() -> Self {
        Self {
            proxy_channel: HashMap::new(),
        }
    }

    pub fn set_local_proxy_channel(&mut self, visitor: String, tcp_stream: TcpStream) {
        self.proxy_channel
            .insert(visitor, Arc::new(Mutex::new(tcp_stream)));
    }

    pub fn get_local_proxy_channel(&mut self, visitor: &str) -> Option<Arc<Mutex<TcpStream>>> {
        self.proxy_channel.get(visitor).cloned()
    }

    pub fn remove_local_proxy_channel(&mut self, visitor: String) -> Option<Arc<Mutex<TcpStream>>> {
        self.proxy_channel.remove(&visitor)
    }
}
