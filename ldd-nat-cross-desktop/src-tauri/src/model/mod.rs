pub mod command;
pub mod protocol;
pub mod proxy;

use crate::model::proxy::ProxyConfig;
use serde::Deserialize;

#[derive(Debug, Deserialize, Clone)]
pub struct ClientConfig {
    pub proxies: Vec<ProxyConfig>,
    #[serde(rename = "serverHost")]
    pub server_host: String,
    #[serde(rename = "serverPort")]
    pub server_port: i32,
    pub password: String,
}

impl ClientConfig {
    pub fn get_proxies(&self) -> Vec<ProxyConfig> {
        self.proxies.clone()
    }
    pub fn get_server_host(&self) -> String {
        self.server_host.clone()
    }
    pub fn get_server_port(&self) -> i32 {
        self.server_port.clone()
    }
    pub fn get_password(&self) -> String {
        self.password.clone()
    }
}
