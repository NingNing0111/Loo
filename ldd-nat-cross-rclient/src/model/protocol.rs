use once_cell::sync::Lazy;
use serde::{Deserialize, Deserializer};
use std::collections::HashMap;

#[derive(Clone, Debug)]
pub enum ProtocolEnum {
    TCP,
    UDP,
    Unknown(String),
}

impl ProtocolEnum {
    pub fn of(value: &str) -> Option<ProtocolEnum> {
        CACHE.get(value).cloned()
    }

    pub fn as_str(&self) -> &str {
        match self {
            ProtocolEnum::TCP => "tcp",
            ProtocolEnum::UDP => "udp",
            ProtocolEnum::Unknown(other) => other.as_str(),
        }
    }
}

pub static CACHE: Lazy<HashMap<String, ProtocolEnum>> = Lazy::new(|| {
    let mut cache = HashMap::new();
    cache.insert("tcp".to_string(), ProtocolEnum::TCP);
    cache.insert("udp".to_string(), ProtocolEnum::UDP);
    cache
});

// 自定义反序列化逻辑（自动将未知值映射为 Unknown）
impl<'de> Deserialize<'de> for ProtocolEnum {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: Deserializer<'de>,
    {
        let value: String = Deserialize::deserialize(deserializer)?;
        match value.to_lowercase().as_str() {
            "tcp" => Ok(ProtocolEnum::TCP),
            "udp" => Ok(ProtocolEnum::UDP),
            other => Ok(ProtocolEnum::Unknown(other.to_string())),
        }
    }
}
