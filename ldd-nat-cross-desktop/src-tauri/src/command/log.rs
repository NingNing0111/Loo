use crate::{
    model::{
        command::CommandResult,
        dto::{ConnectLog, PageResult},
    },
    store::{connect_log_dao::ConnectLogDAO, model::log::ConnectLogDO},
};

#[tauri::command]
pub fn add_connect_log(connect_log: ConnectLog) -> CommandResult<Option<ConnectLogDO>> {
    let log_dao = ConnectLogDAO::new();
    let entity = connect_log.to_entity();
    let id = log_dao.insert(entity).unwrap();
    match log_dao.find_by_id(id) {
        Ok(r) => CommandResult::ok_with_msg_data("连接日志记录成功", r),
        Err(_) => CommandResult::err("连接日志记录失败"),
    }
}

#[tauri::command]
pub fn update_connect_log(connect_log: ConnectLogDO) -> CommandResult<usize> {
    let log_dao = ConnectLogDAO::new();
    match log_dao.update_by_id(connect_log) {
        Ok(r) => CommandResult::ok_with_msg_data("连接日志更新成功", r),
        Err(_) => CommandResult::err("连接日志更新失败"),
    }
}

#[tauri::command]
pub fn page_connect_log(page: i32, page_size: i32) -> CommandResult<PageResult<ConnectLogDO>> {
    let log_dao = ConnectLogDAO::new();
    match log_dao.page(page, page_size) {
        Ok(r) => CommandResult::ok_with_msg_data("查询成功", r),
        Err(_) => CommandResult::err("查询失败"),
    }
}
