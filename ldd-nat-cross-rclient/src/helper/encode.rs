use bytes::BytesMut;
use prost::Message;
use std::error::Error;

use crate::core::transfer_message::TransferDataMessage;

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

/// 消息编码器
///
/// # 参数
///
/// - `message`: 发送的对象，通过protobuf构建出的
///
/// # 返回值
///
/// 一个BytesMut，可以直接写入到连接通道中
pub fn encode_message(message: TransferDataMessage) -> Result<BytesMut, Box<dyn Error>> {
    let mut message_buf = BytesMut::new();
    message.encode_length_delimited(&mut message_buf)?;
    Ok(message_buf)
}
