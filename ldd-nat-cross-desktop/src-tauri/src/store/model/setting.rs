use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct SettingInfoDO {
    pub theme: String,    // 主题
    pub language: String, // 语言
    pub compact: bool,    // 紧凑性
}
