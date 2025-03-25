use crate::{
    common::constants::{ERR, NORMAL},
    store::{log_dao::LogDAO, model::log::LogDO},
    utils::time::now_timestamp,
};

pub fn build_normal_log(operation: i32, description: &str) -> LogDO {
    LogDO {
        id: None,
        operation,
        log_type: NORMAL,
        description: String::from(description),
        created_time: Some(now_timestamp()),
    }
}

pub fn build_err_log(operation: i32, description: &str) -> LogDO {
    LogDO {
        id: None,
        operation,
        log_type: ERR,
        description: String::from(description),
        created_time: Some(now_timestamp()),
    }
}

pub fn add_normal_log(operation: i32, description: &str) {
    let log_dao = LogDAO::new();
    let log_do = build_normal_log(operation, description);
    let _ = log_dao.insert(log_do);
}

pub fn add_err_log(operation: i32, description: &str) {
    let log_dao = LogDAO::new();
    let log_do = build_err_log(operation, description);
    let _ = log_dao.insert(log_do);
}
