use std::str::FromStr;

/// 数组转字符串，例如 [1,2,3] => "1,2,3"
pub fn num_array_to_string<T: ToString>(nums: Vec<T>) -> String {
    nums.into_iter()
        .map(|n| n.to_string())
        .collect::<Vec<String>>()
        .join(",")
}

/// 字符串转数组，例如 "1,2,3" => [1,2,3]
pub fn string_to_num_array<T: FromStr>(s: &str) -> Vec<T> {
    s.split(',').filter_map(|x| x.parse::<T>().ok()).collect()
}
