use std::str;

use clap::Parser;

#[derive(Parser, Debug)]
#[command(version, about, long_about = None)]
pub struct Args {
    /// config file path
    #[arg(short, long)]
    config: String,
}

impl Args {
    pub fn get_config_path(&self) -> &str {
        &self.config
    }
}

pub fn get_args() -> Args {
    let args = Args::parse();
    return args;
}
