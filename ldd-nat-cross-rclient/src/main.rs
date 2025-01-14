use std::error::Error;

use ldd_nat_cross_rclient::{
    client::client::Client,
    config::{arg::get_args, client::get_config, log::init_log},
};

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let args = get_args();
    let config_file_path = args.get_config_path();
    let all_config = get_config(config_file_path).expect("parse config file fail!");
    let log_config = all_config.get_log_config();
    init_log(log_config).expect("init log config fail!");

    let client_config = all_config.get_client_config().clone();
    let mut client = Client::new(client_config);
    client.start().await?;
    Ok(())
}
