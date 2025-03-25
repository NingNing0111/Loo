use crate::client::ClientApp;
use std::sync::Arc;
use tokio::sync::Mutex;

pub const CONFIG_DB: &str = "config.db";
pub const LOG_DB: &str = "log.db";
pub const USER_DB: &str = "user.db";

pub struct AppState {
    pub client: Arc<Mutex<Option<ClientApp>>>,
    pub name: String,
}

impl AppState {
    pub fn new(app_name: impl Into<String>) -> Self {
        Self {
            client: Arc::new(Mutex::new(None)),
            name: app_name.into(),
        }
    }

    pub fn app_name(&self) -> &str {
        &self.name
    }
}
