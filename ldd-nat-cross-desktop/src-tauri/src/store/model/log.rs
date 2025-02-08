use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct ConnectLogDO {
    pub id: Option<i32>,
    #[serde(rename = "serverAddr")]
    pub server_addr: String,
    #[serde(rename = "proxyAddr")]
    pub proxy_addr: String,
    #[serde(rename = "visitorAddr")]
    pub visitor_addr: Option<String>,
    pub status: i32, // 0: 失败 1: 成功
    #[serde(rename = "connectedTime")]
    pub connected_time: Option<i64>,
    #[serde(rename = "disconnectedTime")]
    pub disconnected_time: Option<i64>,
    #[serde(rename = "createdTime")]
    pub created_time: i64,
}
