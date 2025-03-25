use std::error::Error;

use chrono::Utc;
use fern::Dispatch;
use serde::Deserialize;

#[derive(Debug, Deserialize)]
pub struct LogConfig {
    #[serde(rename = "errorPath", default = "default_error_path")]
    error_path: String,
    #[serde(default = "default_path")]
    path: String,
}

fn default_error_path() -> String {
    "error.log".to_string()
}

fn default_path() -> String {
    "client.log".to_string()
}

impl LogConfig {
    pub fn new(error_path: String, path: String) -> Self {
        Self { error_path, path }
    }
    pub fn get_error_path(&self) -> &str {
        self.error_path.as_str()
    }
    pub fn get_path(&self) -> &str {
        self.path.as_str()
    }
}

pub fn init_log(log_config: &LogConfig) -> Result<(), Box<dyn Error>> {
    let error_path = log_config.get_error_path().to_string();
    let path = log_config.get_path().to_string();

    // 配置App日志输出到文件
    let file_dispatch = Dispatch::new()
        .chain(
            std::fs::OpenOptions::new()
                .create(true)
                .append(true)
                .open(path)?,
        )
        .format(move |out, message, record| {
            out.finish(format_args!(
                "[{}][{}][{}] {}",
                Utc::now().format("%Y-%m-%d %H:%M:%S"), // 时间戳
                record.level(),
                record.target(),
                message
            ))
        })
        .level(log::LevelFilter::Info);

    // 设置错误日志输出格式
    let error_log_dispatch = Dispatch::new()
        .chain(
            std::fs::OpenOptions::new()
                .create(true)
                .append(true)
                .open(error_path)?,
        )
        .format(move |out, message, record| {
            out.finish(format_args!(
                "[{}][{}][{}] {}",
                Utc::now().format("%Y-%m-%d %H:%M:%S"), // 时间戳
                record.level(),
                record.target(),
                message
            ))
        })
        .level(log::LevelFilter::Error);

    // 设置控制台输出
    let console_log_dispatch = Dispatch::new()
        .chain(std::io::stdout()) // 输出到控制台
        .format(move |out, message, record| {
            out.finish(format_args!(
                "[{}][{}] {}",
                Utc::now().format("%Y-%m-%d %H:%M:%S"), // 时间戳
                record.level(),
                message
            ))
        })
        .level(log::LevelFilter::Info); // 控制台只打印 info 级别及以上日志
                                        // 配置日志输出到控制台和文件
    fern::Dispatch::new()
        .chain(console_log_dispatch) // 控制台输出
        .chain(file_dispatch) // 文件输出
        .chain(error_log_dispatch)
        .apply()?;

    Ok(())
}
