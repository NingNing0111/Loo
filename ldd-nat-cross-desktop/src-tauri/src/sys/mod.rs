use std::{env, fs, path::PathBuf};

use crate::global::APP_NAME;

pub fn init_app_dir() {
    let app_dir = get_app_dir();

    if !app_dir.exists() {
        fs::create_dir_all(&app_dir).ok(); // 创建应用目录
    }
}

pub fn get_app_dir() -> PathBuf {
    let mut user_dir = dirs::home_dir().unwrap_or_else(|| env::temp_dir());
    user_dir.push(format!(".{}", APP_NAME));
    user_dir
}

pub fn get_db_path(db_name: &str) -> PathBuf {
    let mut app_dir = get_app_dir();
    app_dir.push("data");
    if !app_dir.exists() {
        let _ = fs::create_dir_all(&app_dir);
    }
    app_dir.push(db_name);
    if !app_dir.exists() {
        let _ = fs::File::create(&app_dir);
    }

    app_dir
}
