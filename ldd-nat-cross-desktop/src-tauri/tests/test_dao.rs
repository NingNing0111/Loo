use app_lib::store::{model::config::ServerConfig, server_config_dao::ServerConfigDAO};

#[test]
pub fn test_new() {
    let server_config_dao = ServerConfigDAO::new();
    let s = server_config_dao.find_all().unwrap();
    println!("len: {}", s.len())
}

#[test]
pub fn test_reset() {
    let server_config_dao = ServerConfigDAO::new();
    server_config_dao.reset_data().unwrap();
}

#[test]
pub fn test_insert() {
    let server_config_dao = ServerConfigDAO::new();
    let config = ServerConfig {
        id: None,
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
