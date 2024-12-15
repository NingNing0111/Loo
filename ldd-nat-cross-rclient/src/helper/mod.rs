use std::collections::HashMap;

use bytes::BytesMut;
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

/// 编码 Varint 格式长度
pub fn encode_varint(mut value: u32) -> Vec<u8> {
    let mut buf = Vec::new();
    while value >= 0x80 {
        buf.push((value as u8 & 0x7F) | 0x80);
        value >>= 7;
    }
    buf.push(value as u8);
    buf
}

/// 解码 Varint 格式长度前缀
pub fn decode_varint(buf: &mut BytesMut) -> Option<(u32, BytesMut)> {
    let mut length = 0u32;
    let mut shift = 0;

    for (i, &byte) in buf.iter().enumerate() {
        length |= ((byte & 0x7F) as u32) << shift;
        if byte & 0x80 == 0 {
            let remaining = buf.split_off(i + 1);
            return Some((length, remaining));
        }
        shift += 7;
    }

    None
}
