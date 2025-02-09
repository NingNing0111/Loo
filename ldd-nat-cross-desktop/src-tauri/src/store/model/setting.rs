use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct SettingInfoDO {
    pub theme: String,
    pub language: String,
    pub compact: bool,
}
