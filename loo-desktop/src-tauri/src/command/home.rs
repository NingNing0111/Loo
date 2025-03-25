use crate::{
    model::{command::CommandResult, dto::HomeCntInfo},
    store::{
        log_dao::LogDAO, proxy_config_dao::ProxyConfigDAO, server_config_dao::ServerConfigDAO,
    },
};

#[tauri::command]
pub fn count_info() -> CommandResult<HomeCntInfo> {
    let server_dao = ServerConfigDAO::new();
    let proxy_dao = ProxyConfigDAO::new();
    let log_dao = LogDAO::new();

    let server_cnt = match server_dao.count() {
        Ok(c) => c,
        Err(_) => 0,
    };
    let proxy_cnt = match proxy_dao.count() {
        Ok(c) => c,
        Err(_) => 0,
    };
    let successed_cnt = match log_dao.count_normal() {
        Ok(c) => c,
        Err(_) => 0,
    };
    let failed_cnt = match log_dao.count_err() {
        Ok(c) => c,
        Err(_) => 0,
    };

    CommandResult::ok_with_data(HomeCntInfo {
        server_cnt,
        proxy_cnt,
        successed_cnt,
        failed_cnt,
    })
}
