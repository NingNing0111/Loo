use ldd_nat_cross_rclient::{
    client::ClientApp,
    config::{arg::get_args, client::get_config, log::init_log},
};

#[tokio::main]
async fn main() {
    let args = get_args();
    let config_file_path = args.get_config_path();
    let all_config = get_config(config_file_path).expect("parse config file fail!");
    let log_config = all_config.get_log_config();
    init_log(log_config).expect("init log config fail!");

    let client_config = all_config.get_client_config();

    let mut client_app = ClientApp::new(client_config.clone());

    client_app.start().await.unwrap();
}
