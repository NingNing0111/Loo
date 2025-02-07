#[derive(Debug, Clone)]
pub struct ServerConfig {
    pub id: Option<i32>,
    pub server_host: String,
    pub server_port: i32,
    pub password: String,
    pub create_time: Option<i64>,
}
