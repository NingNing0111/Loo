use crate::{
    model::command::CommandResult,
    store::{model::setting::SettingInfoDO, setting_dao::SettingInfoDAO},
};

#[tauri::command]
pub fn get_setting() -> CommandResult<Option<SettingInfoDO>> {
    let setting_dao = SettingInfoDAO::new();
    match setting_dao.get(1) {
        Ok(r) => CommandResult::ok_with_msg_data("查询成功", r),
        Err(_) => CommandResult::err("查询失败"),
    }
}

#[tauri::command]
pub fn update_setting(setting_info: SettingInfoDO) -> CommandResult<usize> {
    let setting_dao = SettingInfoDAO::new();
    match setting_dao.update(setting_info) {
        Ok(r) => CommandResult::ok_with_msg_data("更新成功", r),
        Err(_) => CommandResult::err("更新失败"),
    }
}

#[tauri::command]
pub fn reset_setting() -> CommandResult<usize> {
    let setting_dao = SettingInfoDAO::new();
    match setting_dao.reset_data() {
        Ok(r) => CommandResult::ok_with_msg_data("初始化成功", r),
        Err(_) => CommandResult::err("初始化失败"),
    }
}
