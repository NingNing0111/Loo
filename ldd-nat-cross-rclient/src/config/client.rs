use serde::Deserialize;
use std::error::Error;
use std::fs;

use crate::model::proxy::ProxyConfig;

#[derive(Debug, Deserialize)]
pub struct ClientConfig {
    proxies: Vec<ProxyConfig>,
    #[serde(rename = "serverHost")]
    server_host: String,
    #[serde(rename = "serverPort")]
    server_port: i32,
    password: String,
}

impl ClientConfig {
    pub fn new(server_host: String, server_port: i32, password: String) -> Self {
        ClientConfig {
            proxies: Vec::new(),
            server_host,
            server_port,
            password,
        }
    }

    pub fn add_proxy(&mut self, proxy: ProxyConfig) {
        self.proxies.push(proxy);
    }

    pub fn get_proxy(&self) -> &Vec<ProxyConfig> {
        &self.proxies
    }

    pub fn get_server_host(&self) -> &str {
        &self.server_host
    }

    pub fn get_server_port(&self) -> i32 {
        self.server_port
    }

    pub fn get_password(&self) -> &str {
        &self.password
    }
}

#[derive(Debug, Deserialize)]
struct ConfigWrapper {
    client: ClientConfig,
}

pub fn load_config(file_path: &str) -> Result<ClientConfig, Box<dyn Error>> {
    // 读取文件内容
    let yaml_content = fs::read_to_string(file_path)?;
    // 反序列化 YAML 内容
    let config_wrapper: ConfigWrapper = serde_yaml::from_str(&yaml_content)?;
    Ok(config_wrapper.client)
}
