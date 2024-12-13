use std::{
    collections::HashMap,
    io::Read,
    net::TcpStream,
    sync::{Arc, Mutex},
};

use prost::Message;

use crate::{
    common::constants::LICENSE_KEY,
    core::{
        cmd_type::CmdType,
        meta_data::TransferMessageMetaData,
        transfer_message::{self, TransferDataMessage},
    },
};

struct LocalProxyHandler {
    server_channel: Arc<Mutex<TcpStream>>,
    meta_data: Arc<HashMap<String, String>>,
}

impl LocalProxyHandler {
    pub fn new(server_channel: TcpStream, meta_data: HashMap<String, String>) -> Self {
        Self {
            server_channel: Arc::new(Mutex::new(server_channel)),
            meta_data: Arc::new(meta_data),
        }
    }
}
