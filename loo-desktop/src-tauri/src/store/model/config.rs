use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct ServerConfigDO {
    pub id: Option<i32>,
    pub label: String,
    #[serde(rename = "serverHost")]
    pub server_host: String,
    #[serde(rename = "serverPort")]
    pub server_port: i32,
    pub password: String,
    #[serde(rename = "createTime")]
    pub create_time: Option<i64>,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct ProxyConfigDO {
    pub id: Option<i32>,
    pub label: String,
    pub host: String,
    pub port: i32,
    #[serde(rename = "openPort")]
    pub open_port: i32,
    pub protocol: String,
    #[serde(rename = "createTime")]
    pub create_time: Option<i64>,
}
