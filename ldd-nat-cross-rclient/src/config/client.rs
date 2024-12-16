use serde::Deserialize;
use std::error::Error;
use std::fs;

use crate::model::proxy::ProxyConfig;

use super::log::LogConfig;

#[derive(Debug, Deserialize, Clone)]
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
pub struct ConfigWrapper {
    client: ClientConfig,
    #[serde(default = "default_log_config")]
    log: LogConfig,
}

fn default_log_config() -> LogConfig {
    LogConfig::new("error.log".to_string(), "client.log".to_string())
}

impl ConfigWrapper {
    pub fn get_client_config(&self) -> &ClientConfig {
        &self.client
    }
    pub fn get_log_config(&self) -> &LogConfig {
        &self.log
    }
}

pub fn get_config(file_path: &str) -> Result<ConfigWrapper, Box<dyn Error>> {
    // 读取文件内容
    let yaml_content = fs::read_to_string(file_path)?;
    // 反序列化 YAML 内容
    let config_wrapper: ConfigWrapper = serde_yaml::from_str(&yaml_content)?;
    Ok(config_wrapper)
}
