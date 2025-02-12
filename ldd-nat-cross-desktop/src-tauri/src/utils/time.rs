use chrono::Utc;

/// 当前时间戳
pub fn now_timestamp() -> i64 {
    let now = Utc::now();
    now.timestamp()
}
