use bytes::BytesMut;
use clap::error;
use prost::{decode_length_delimiter, Message};
use std::error::Error;

use crate::core::transfer_message::TransferDataMessage;

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
    if let Some((msg_len, mut remaining)) = decode_varint(read_buf) {
        if remaining.len() >= msg_len as usize {
            let msg_bytes = remaining.split_to(msg_len as usize);
            let received_msg = TransferDataMessage::decode(&msg_bytes[..])?;
            return Ok(received_msg);
        } else {
            // 错误：数据不足
            return Err(Box::new(std::io::Error::new(
                std::io::ErrorKind::UnexpectedEof,
                "Insufficient data to decode message",
            )));
        }
    }
    // 错误：无法解码 VarInt
    Err(Box::new(std::io::Error::new(
        std::io::ErrorKind::InvalidData,
        "Failed to decode varint from buffer",
    )))
}
