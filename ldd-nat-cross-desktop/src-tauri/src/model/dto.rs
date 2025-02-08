use serde::{Deserialize, Serialize};

use crate::store::model::{config::ProxyConfigDO, log::ConnectLogDO};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct HomeCntInfo {
    #[serde(rename = "serverCnt")]
    pub server_cnt: i64,
    #[serde(rename = "proxyCnt")]
    pub proxy_cnt: i64,
    #[serde(rename = "successedCnt")]
    pub successed_cnt: i64,
    #[serde(rename = "failedCnt")]
    pub failed_cnt: i64,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct ConnectLog {
    #[serde(rename = "serverAddr")]
    pub server_addr: String,
    #[serde(rename = "proxyAddr")]
    pub proxy_addr: String,
    #[serde(rename = "visitorAddr")]
    pub visitor_addr: Option<String>,
    pub status: i32,
    #[serde(rename = "connectedTime")]
    pub connected_time: Option<i64>,
    #[serde(rename = "disconnectedTime")]
    pub disconnected_time: Option<i64>,
}

impl ConnectLog {
    pub fn to_entity(&self) -> ConnectLogDO {
        ConnectLogDO {
            id: None,
            server_addr: self.server_addr.clone(),
            proxy_addr: self.proxy_addr.clone(),
            visitor_addr: self.visitor_addr.clone(),
            status: self.status,
            connected_time: self.connected_time,
            disconnected_time: self.disconnected_time,
            created_time: 0,
        }
    }
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct PageResult<T> {
    total: i64,
    records: Option<Vec<T>>,
}

impl<T> PageResult<T> {
    pub fn new(total: i64, records: Vec<T>) -> Self {
        PageResult {
            total,
            records: Some(records),
        }
    }
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct AddProxyConfig {
    pub proxies: Vec<ProxyConfigDO>,
}
