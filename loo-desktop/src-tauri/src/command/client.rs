use tauri::AppHandle;

use crate::{
    client::ClientApp,
    common::constants::APP_DISCONNECT,
    helper::log::{add_err_log, add_normal_log},
    model::{command::CommandResult, ClientConfig},
    APP_STATE,
};

// 启动应用
#[tauri::command]
pub async fn start_app(app: AppHandle, config: ClientConfig) -> CommandResult<()> {
    let client_clone = APP_STATE.client.clone();

    let client_app = ClientApp::new(config);

    // 存储 ClientApp 实例
    {
        let mut client_lock = client_clone.lock().await;
        *client_lock = Some(client_app);
    }

    // 再次获取锁并启动 app
    let mut client_lock = client_clone.lock().await;
    if let Some(client_app) = client_lock.as_mut() {
        if let Err(e) = client_app.listener_err(app).await {
            return CommandResult::err(e.to_string().as_str());
        }

        if let Err(e) = client_app.start().await {
            return CommandResult::err(e.to_string().as_str());
        }
    }

    CommandResult::ok("服务启动成功")
}

// 停止应用
#[tauri::command]
pub async fn stop_app() -> CommandResult<()> {
    let mut client_lock = APP_STATE.client.lock().await;
    if let Some(app) = client_lock.take() {
        if let Err(e) = app.stop().await {
            add_err_log(APP_DISCONNECT, &format!("关闭内网穿透异常:{:?}", e));
            return CommandResult::err(e.to_string().as_str());
        }
    } else {
        add_normal_log(APP_DISCONNECT, "No running app to stop.");
        return CommandResult::err("No running app to stop.");
    }
    add_normal_log(APP_DISCONNECT, "关闭服务成功");
    CommandResult::ok("服务已停止")
}
