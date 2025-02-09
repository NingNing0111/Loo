use serde::{Deserialize, Serialize};

use crate::store::model::config::{ProxyConfigDO, ServerConfigDO};

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

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct AppConfig {
    pub proxies: Vec<ProxyConfigDO>,
    pub server: Option<ServerConfigDO>,
}
