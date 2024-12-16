use std::collections::HashMap;

use tokio::net::TcpStream;

pub struct Client;

pub struct ClientManager {
    proxy_channel: HashMap<String, TcpStream>,
}

impl ClientManager {
    pub fn new() -> Self {
        Self {
            proxy_channel: HashMap::new(),
        }
    }

    pub fn set_local_proxy_channel(&mut self, visitor: String, tcp_stream: TcpStream) {
        self.proxy_channel.insert(visitor, tcp_stream);
    }

    pub fn get_local_proxy_channel(&mut self, visitor: String) -> Option<&TcpStream> {
        let channels = &self.proxy_channel;
        channels.get(&visitor)
    }

    pub fn remove_local_proxy_channel(&mut self, visitor: String) -> Option<TcpStream> {
        self.proxy_channel.remove(&visitor)
    }
}
