use prost_types::Timestamp;
use std::collections::HashMap;

use crate::{
    common::constants::{AUTH_PASSWORD, LICENSE_KEY, VISITOR_ID},
    core::{
        cmd_type::CmdType, meta_data::TransferMessageMetaData,
        transfer_message::TransferDataMessage,
    },
    model::proxy::ProxyConfig,
};
/// 构建认证消息
pub fn build_auth_message(password: &str) -> TransferDataMessage {
    let mut meta_map: HashMap<String, String> = HashMap::new();
    meta_map.insert(String::from(AUTH_PASSWORD), String::from(password));

    let auth_meta = TransferMessageMetaData {
        timestamp: Some(Timestamp::default()),
        meta_data: meta_map,
    };

    let auth_message = TransferDataMessage {
        cmd_type: CmdType::Auth as i32,
        meta_data: Some(auth_meta),
        data: [].to_vec(),
    };
    auth_message
}

/// 构建开放代理消息
pub fn build_open_server_message(
    proxy_config: &ProxyConfig,
    license_key: String,
) -> TransferDataMessage {
    let mut meta_map = proxy_config.to_map();
    meta_map.insert(String::from(LICENSE_KEY), String::from(license_key));

    let open_server_meta = TransferMessageMetaData {
        timestamp: Some(Timestamp::default()),
        meta_data: meta_map,
    };

    let open_server_message = TransferDataMessage {
        cmd_type: CmdType::OpenServer as i32,
        meta_data: Some(open_server_meta),
        data: [].to_vec(),
    };
    open_server_message
}

/// 构建连接消息
pub fn build_connect_message(
    proxy_config: ProxyConfig,
    license_key: String,
    visitor_id: String,
) -> TransferDataMessage {
    let mut meta_map = proxy_config.to_map();
    meta_map.insert(String::from(LICENSE_KEY), String::from(license_key));
    meta_map.insert(VISITOR_ID.to_string(), visitor_id);

    let meta_data = TransferMessageMetaData {
        timestamp: Some(Timestamp::default()),
        meta_data: meta_map,
    };
    let connect_message = TransferDataMessage {
        cmd_type: CmdType::Connect as i32,
        meta_data: Some(meta_data),
        data: [].to_vec(),
    };
    connect_message
}

/// 构建断开连接消息
pub fn build_disconnect_message(license_key: String, visitor_id: String) -> TransferDataMessage {
    let mut meta_map = HashMap::new();
    meta_map.insert(LICENSE_KEY.to_string(), license_key);
    meta_map.insert(VISITOR_ID.to_string(), visitor_id);

    let meta_data = TransferMessageMetaData {
        timestamp: Some(Timestamp::default()),
        meta_data: meta_map,
    };

    TransferDataMessage {
        cmd_type: CmdType::Disconnect as i32,
        meta_data: Some(meta_data),
        data: [].to_vec(),
    }
}

/// 构建传输消息
pub fn build_transfer_message(
    data: Vec<u8>,
    visitor_id: String,
    license_key: String,
) -> TransferDataMessage {
    let mut meta_map = HashMap::new();
    meta_map.insert(VISITOR_ID.to_string(), visitor_id);
    meta_map.insert(LICENSE_KEY.to_string(), license_key);

    let mt_data = TransferMessageMetaData {
        timestamp: Some(Timestamp::default()),
        meta_data: meta_map,
    };
    TransferDataMessage {
        cmd_type: CmdType::Transfer as i32,
        meta_data: Some(mt_data),
        data,
    }
}
