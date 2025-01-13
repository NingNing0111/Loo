use bytes::BytesMut;
use prost::{DecodeError, Message};
use std::{error::Error, fs::read};

use crate::core::transfer_message::TransferDataMessage;

/// 解码 Varint 格式长度前缀
/// 未用到
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

/// 消息解码器
///
/// # 参数
///
/// - `read_buf`: 连接通道内读取到的BytesMut
///
/// # 返回值
///
/// 一个TransferDataMessage类型的变量
pub fn decode_message(read_buf: &mut BytesMut) -> Result<TransferDataMessage, Box<dyn Error>> {
    let msg = TransferDataMessage::decode_length_delimited(read_buf)?;
    Ok(msg)
}
