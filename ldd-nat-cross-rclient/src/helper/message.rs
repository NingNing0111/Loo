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

pub fn build_open_server_message(
    proxy_config: ProxyConfig,
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
