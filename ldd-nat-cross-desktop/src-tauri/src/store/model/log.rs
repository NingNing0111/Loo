use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct LogDO {
    pub id: Option<i32>,
    pub operation: i32, // 操作类型 0: 连接操作 1:断开操作
    #[serde(rename = "logType")]
    pub log_type: i32, // 日志类型 0: 一般日志 1: 错误日志
    pub description: String, // 日志内容描述
    #[serde(rename = "createdTime")]
    pub created_time: Option<i64>,
}
