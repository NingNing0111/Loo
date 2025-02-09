use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct ConnectLogDO {
    pub id: Option<i32>,
    #[serde(rename = "serverId")]
    pub server_id: i32,
    #[serde(rename = "proxyIds")]
    pub proxy_ids: Vec<i32>,
    pub operation: i32, // 0: 连接操作 1:断开操作
    pub status: i32,    // 0: 连接失败 1: 连接成功
    #[serde(rename = "connectedTime")]
    pub connected_time: Option<i64>,
    #[serde(rename = "createdTime")]
    pub created_time: Option<i64>,
}
