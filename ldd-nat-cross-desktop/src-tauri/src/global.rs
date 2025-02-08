use std::sync::Arc;

use once_cell::sync::Lazy;
use tokio::sync::Mutex;

use crate::client::ClientApp;

pub const APP_NAME: &str = "ldd-nat-cross";
pub const CONFIG_DB: &str = "config.db";
pub const LOG_DB: &str = "log.db";

// 使用 Arc<Mutex<Option<ClientApp>>> 来存储 ClientApp
// 使用 tokio::sync::Mutex 让 ClientApp 可以在异步任务中安全访问
pub struct AppState {
    pub client: Arc<Mutex<Option<ClientApp>>>,
    pub name: Arc<Option<String>>,
}

impl AppState {
    fn new() -> Self {
        Self {
            client: Arc::new(Mutex::new(None)),
            name: Arc::new(Some(String::from(APP_NAME))),
        }
    }
}

pub static APP_STATE: Lazy<AppState> = Lazy::new(|| AppState::new());
