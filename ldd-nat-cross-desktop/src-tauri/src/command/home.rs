use crate::{
    model::{command::CommandResult, dto::HomeCntInfo},
    store::{
        connect_log_dao::ConnectLogDAO, proxy_config_dao::ProxyConfigDAO,
        server_config_dao::ServerConfigDAO,
    },
};

#[tauri::command]
pub fn count_info() -> CommandResult<HomeCntInfo> {
    let server_dao = ServerConfigDAO::new();
    let proxy_dao = ProxyConfigDAO::new();
    let log_dao = ConnectLogDAO::new();

    let server_cnt = match server_dao.count() {
        Ok(c) => c,
        Err(_) => 0,
    };
    let proxy_cnt = match proxy_dao.count() {
        Ok(c) => c,
        Err(_) => 0,
    };
    let successed_cnt = match log_dao.count_success() {
        Ok(c) => c,
        Err(_) => 0,
    };
    let failed_cnt = match log_dao.count_fail() {
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
