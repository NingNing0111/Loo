use serde::{Deserialize, Serialize};

const OK_CODE: i32 = 0;
const ERR_CODE: i32 = 10000;

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct CommandResult<T> {
    code: i32,
    msg: Option<String>,
    err: Option<String>,
    data: Option<T>,
}

impl<T> CommandResult<T> {
    pub fn ok(msg: &str) -> Self {
        CommandResult {
            code: OK_CODE,
            msg: Some(msg.to_string()),
            err: None,
            data: None,
        }
    }

    pub fn err(err: &str) -> Self {
        CommandResult {
            code: ERR_CODE,
            msg: None,
            err: Some(err.to_string()),
            data: None,
        }
    }

    pub fn custom_err(err: &str, err_code: i32) -> Self {
        CommandResult {
            code: ERR_CODE + err_code,
            msg: None,
            err: Some(err.to_string()),
            data: None,
        }
    }

    pub fn ok_with_data(data: T) -> Self {
        CommandResult {
            code: OK_CODE,
            msg: None,
            err: None,
            data: Some(data),
        }
    }

    pub fn ok_with_msg_data(msg: &str, data: T) -> Self {
        CommandResult {
            code: OK_CODE,
            msg: Some(msg.to_string()),
            err: None,
            data: Some(data),
        }
    }
}
