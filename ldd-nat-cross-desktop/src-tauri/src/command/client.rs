use crate::{
    client::ClientApp,
    global::APP_STATE,
    model::{command::CommandResult, dto::AppConfig, ClientConfig},
    store::{
        connect_log_dao::ConnectLogDAO, model::config::ProxyConfigDO,
        proxy_config_dao::ProxyConfigDAO, server_config_dao::ServerConfigDAO,
    },
};

// 启动应用
#[tauri::command]
pub async fn start_app(config: ClientConfig) -> CommandResult<()> {
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
pub async fn stop_app() -> CommandResult<()> {
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

#[tauri::command]
pub async fn last_config() -> CommandResult<Option<AppConfig>> {
    let log_dao = ConnectLogDAO::new();
    let server_dao = ServerConfigDAO::new();
    let proxy_dao = ProxyConfigDAO::new();
    let last_res = log_dao.last_connect().unwrap();
    match last_res {
        Some(last_conn) => {
            let server = server_dao.find_by_id(last_conn.server_id).unwrap();
            let mut proxies = Vec::<ProxyConfigDO>::new();
            for proxy_id in last_conn.proxy_ids {
                let proxy = proxy_dao.find_by_id(proxy_id).unwrap();
                if proxy.is_some() {
                    proxies.push(proxy.unwrap());
                }
            }
            CommandResult::ok_with_data(Some(AppConfig { server, proxies }))
        }
        None => CommandResult::err("无数据"),
    }
}
