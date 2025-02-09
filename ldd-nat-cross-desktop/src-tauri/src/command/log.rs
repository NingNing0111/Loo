use crate::{
    model::{command::CommandResult, dto::PageResult},
    store::{connect_log_dao::ConnectLogDAO, model::log::ConnectLogDO},
};

#[tauri::command]
pub fn add_connect_log(log: ConnectLogDO) -> CommandResult<Option<ConnectLogDO>> {
    println!("{:?}", log);
    let log_dao = ConnectLogDAO::new();
    let id = log_dao.insert(log).unwrap();
    match log_dao.find_by_id(id) {
        Ok(r) => CommandResult::ok_with_msg_data("连接日志记录成功", r),
        Err(_) => CommandResult::err("连接日志记录失败"),
    }
}

#[tauri::command]
pub fn page_connect_log(page: i32, page_size: i32) -> CommandResult<PageResult<ConnectLogDO>> {
    let log_dao: ConnectLogDAO = ConnectLogDAO::new();
    match log_dao.page(page, page_size) {
        Ok(r) => CommandResult::ok_with_msg_data("查询成功", r),
        Err(_) => CommandResult::err("查询失败"),
    }
}
