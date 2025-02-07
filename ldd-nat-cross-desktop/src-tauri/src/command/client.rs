use crate::{
    client::ClientApp,
    global::APP_STATE,
    model::{command::CommandResult, ClientConfig},
};

// 启动应用
#[tauri::command]
pub async fn start_app(config: ClientConfig) -> CommandResult {
    let client_clone = APP_STATE.client.clone();

    let app = ClientApp::new(config);

    // 存储 ClientApp 实例
    {
        let mut client_lock = client_clone.lock().await;
        *client_lock = Some(app);
    }

    // 再次获取锁并启动 app
    let mut client_lock = client_clone.lock().await;
    if let Some(app) = client_lock.as_mut() {
        if let Err(e) = app.start().await {
            return CommandResult::err(e.to_string().as_str());
        }
    }
    CommandResult::ok("服务启动成功")
}

// 停止应用
#[tauri::command]
pub async fn stop_app() -> CommandResult {
    let mut client_lock = APP_STATE.client.lock().await;
    if let Some(app) = client_lock.take() {
        if let Err(e) = app.stop().await {
            return CommandResult::err(e.to_string().as_str());
        }
    } else {
        return CommandResult::err("No running app to stop.");
    }
    CommandResult::ok("服务已停止")
}
