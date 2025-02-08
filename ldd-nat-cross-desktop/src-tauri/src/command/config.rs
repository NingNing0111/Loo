use crate::{
    model::{
        command::CommandResult,
        dto::{AddProxyConfig, PageResult},
        ClientConfig,
    },
    store::{
        model::config::{ProxyConfigDO, ServerConfigDO},
        proxy_config_dao::ProxyConfigDAO,
        server_config_dao::ServerConfigDAO,
    },
};

#[tauri::command]
pub fn add_config(client_config: ClientConfig) -> CommandResult<()> {
    let server_dao = ServerConfigDAO::new();
    let mut proxy_dao = ProxyConfigDAO::new();

    // add server config
    let server_config = ServerConfigDO {
        id: None,
        server_host: client_config.server_host,
        server_port: client_config.server_port,
        password: client_config.password,
        create_time: None,
    };

    let server_cnt = server_dao
        .insert(server_config.clone())
        .map_err(|_| CommandResult::<()>::err("insert server config failed."))
        .unwrap();

    // add proxy config
    let proxies = client_config.proxies;
    let proxy_configs: Vec<ProxyConfigDO> = proxies
        .into_iter()
        .map(|proxy_config| ProxyConfigDO {
            id: None,
            host: proxy_config.host().to_string(),
            port: proxy_config.port(),
            open_port: proxy_config.open_port(),
            protocol: proxy_config.protocol().as_str().to_string(),
            create_time: None,
        })
        .collect();

    let proxy_cnt = proxy_dao
        .insert_batch(proxy_configs)
        .map_err(|_| CommandResult::<()>::err("insert proxy config failed."))
        .unwrap();

    CommandResult::ok(&format!(
        "成功添加 {} 条服务端配置 {} 条客户端配置",
        server_cnt, proxy_cnt
    ))
}

#[tauri::command]
pub fn add_server_config(server_config: ServerConfigDO) -> CommandResult<usize> {
    let server_dao = ServerConfigDAO::new();
    let i = server_dao
        .insert(server_config.clone())
        .map_err(|_| CommandResult::<()>::err("insert server config failed."))
        .unwrap();
    CommandResult::ok_with_msg_data("添加成功", i)
}

#[tauri::command]
pub fn page_server_config(page: i32, page_size: i32) -> CommandResult<PageResult<ServerConfigDO>> {
    let server_dao = ServerConfigDAO::new();
    let res = server_dao
        .page(page, page_size)
        .map_err(|_| CommandResult::<()>::err("query failed."))
        .unwrap();
    CommandResult::ok_with_data(res)
}

#[tauri::command]
pub fn add_proxy_config_batch(data: AddProxyConfig) -> CommandResult<usize> {
    let mut proxy_dao = ProxyConfigDAO::new();
    let proxies = data.proxies;
    let res = proxy_dao
        .insert_batch(proxies)
        .map_err(|_| CommandResult::<()>::err("insert proxy config failed."))
        .unwrap();
    CommandResult::ok_with_msg_data(&format!("成功添加{}条配置", res), res)
}

#[tauri::command]
pub fn page_proxy_config(page: i32, page_size: i32) -> CommandResult<PageResult<ProxyConfigDO>> {
    let proxy_dao = ProxyConfigDAO::new();
    let res = proxy_dao
        .page(page, page_size)
        .map_err(|_| CommandResult::<()>::err("query failed."))
        .unwrap();
    CommandResult::ok_with_data(res)
}
