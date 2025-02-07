use sys::init_app_dir;

pub mod client;
pub mod command;
pub mod common;
pub mod core;
pub mod global;
pub mod handler;
pub mod helper;
pub mod model;
pub mod store;
pub mod sys;
pub mod utils;

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .setup(|app| {
            if cfg!(debug_assertions) {
                app.handle().plugin(
                    tauri_plugin_log::Builder::default()
                        .level(log::LevelFilter::Info)
                        .build(),
                )?;
            }

            init_app_dir();

            Ok(())
        })
        .invoke_handler(tauri::generate_handler![
            command::client::start_app,
            command::client::stop_app
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
