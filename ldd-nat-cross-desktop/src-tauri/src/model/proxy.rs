use serde::Deserialize;

use crate::common::constants;
use crate::model::protocol::ProtocolEnum;
use std::collections::HashMap;

#[derive(Debug, Deserialize, Clone)]
pub struct ProxyConfig {
    host: String,
    port: i32,
    #[serde(rename = "openPort")]
    open_port: i32,
    protocol: ProtocolEnum,
}

impl ProxyConfig {
    /// 构造函数
    pub fn new(host: String, port: i32, open_port: i32, protocol: ProtocolEnum) -> Self {
        Self {
            host,
            port,
            open_port,
            protocol,
        }
    }

    pub fn host(&self) -> &str {
        &self.host
    }

    pub fn port(&self) -> i32 {
        self.port
    }

    pub fn open_port(&self) -> i32 {
        self.open_port
    }

    pub fn protocol(&self) -> ProtocolEnum {
        self.protocol.clone()
    }

    pub fn to_map(&self) -> HashMap<String, String> {
        let mut data = HashMap::new();
        data.insert(constants::PROXY_HOST.to_string(), self.host.clone());
        data.insert(constants::PROXY_PORT.to_string(), self.port.to_string());
        data.insert(
            constants::PROXY_PROTOCOL.to_string(),
            self.protocol.as_str().to_string(),
        );
        data.insert(constants::OPEN_PORT.to_string(), self.open_port.to_string());
        data
    }

    pub fn from_map(data: HashMap<String, String>) -> Option<Self> {
        let host = data.get(constants::PROXY_HOST)?.to_string();
        let port = data.get(constants::PROXY_PORT)?.parse().ok()?;
        let protocol = ProtocolEnum::of(data.get(constants::PROXY_PROTOCOL)?.as_str())?;
        let open_port = data.get(constants::OPEN_PORT)?.parse().ok()?;

        Some(Self {
            host,
            port,
            open_port,
            protocol,
        })
    }
}
