use std::collections::HashMap;

use prost_types::Timestamp;

use crate::{
    common::constants::AUTH_PASSWORD,
    core::{
        cmd_type::CmdType, meta_data::TransferMessageMetaData,
        transfer_message::TransferDataMessage,
    },
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
