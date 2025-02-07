use chrono::Utc;

pub fn now_timestamp() -> i64 {
    let now = Utc::now();
    now.timestamp()
}
