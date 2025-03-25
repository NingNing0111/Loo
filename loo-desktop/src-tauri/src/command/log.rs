use crate::{
    model::{command::CommandResult, dto::PageResult},
    store::{log_dao::LogDAO, model::log::LogDO},
};

#[tauri::command]
pub fn page_connect_log(page: i32, page_size: i32) -> CommandResult<PageResult<LogDO>> {
    let log_dao: LogDAO = LogDAO::new();
    match log_dao.page(page, page_size) {
        Ok(r) => CommandResult::ok_with_msg_data("查询成功", r),
        Err(_) => CommandResult::err("查询失败"),
    }
}
