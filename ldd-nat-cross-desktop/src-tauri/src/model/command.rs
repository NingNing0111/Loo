use serde::{Deserialize, Serialize};

const OK_CODE: i32 = 0;
const ERR_CODE: i32 = 10000;

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct CommandResult {
    code: i32,
    msg: String,
    err: String,
}

impl CommandResult {
    pub fn ok(msg: &str) -> Self {
        CommandResult {
            code: OK_CODE,
            msg: msg.to_string(),
            err: String::new(),
        }
    }

    pub fn err(err: &str) -> Self {
        CommandResult {
            code: ERR_CODE,
            msg: String::new(),
            err: err.to_string(),
        }
    }

    pub fn custom_err(err: &str, err_code: i32) -> Self {
        CommandResult {
            code: ERR_CODE + err_code,
            msg: String::new(),
            err: err.to_string(),
        }
    }
}
