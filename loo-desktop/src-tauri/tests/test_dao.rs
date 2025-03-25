use app_lib::store::{
    log_dao::LogDAO, model::config::ServerConfigDO, proxy_config_dao::ProxyConfigDAO, server_config_dao::ServerConfigDAO
};

#[test]
pub fn test_new() {
    let server_config_dao = ServerConfigDAO::new();
    let s = server_config_dao.find_all().unwrap();
    println!("len: {}", s.len())
}

#[test]
pub fn test_server_reset() {
    let server_config_dao = ServerConfigDAO::new();
    server_config_dao.reset_data().unwrap();
}

#[test]
pub fn test_proxy_reset() {
    let proxy_config_dao = ProxyConfigDAO::new();
    proxy_config_dao.reset_data().unwrap();
}

#[test]
pub fn test_log_reset() {
    LogDAO::new().reset_data().unwrap();
}

#[test]
pub fn test_insert() {
    let server_config_dao = ServerConfigDAO::new();
    let config = ServerConfigDO {
        id: None,
        label: "test".to_string(),
        server_host: "localhost".to_string(),
        server_port: 8964,
        password: "123456".to_string(),
        create_time: None,
    };
    let i = server_config_dao.insert(config).unwrap();
    println!("i:{}", i);

    let s = server_config_dao.find_all().unwrap();
    println!("len: {}", s.len())
}

#[test]
pub fn test_find_all() {
    let server_config_dao = ServerConfigDAO::new();
    let configs = server_config_dao.find_all().unwrap();
    for config in configs {
        println!("{:?}", config)
    }
}

#[test]
pub fn test_find_by_id() {
    let server_config_dao = ServerConfigDAO::new();
    let config = server_config_dao.find_by_id(1).unwrap();
    println!("{:?}", config);
}
