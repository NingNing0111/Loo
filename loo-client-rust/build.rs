use std::fs;

fn main() {
    let out_dir = "src/core";

    // 创建输出目录
    fs::create_dir_all(out_dir).unwrap();

    prost_build::Config::new()
        .out_dir(out_dir) // 指定输出目录
        .compile_protos(
            &[
                "proto/cmd_type.proto",
                "proto/meta_data.proto",
                "proto/transfer_message.proto",
            ], // .proto 文件
            &["proto/"], // 包含目录
        )
        .expect("Failed to compile .proto files");

    // 创建顶级模块
    let mod_file = format!("{}/mod.rs", out_dir);
    let mod_content = ["cmd_type.rs", "meta_data.rs", "transfer_message.rs"]
        .iter()
        .map(|file| format!("pub mod {};", file.trim_end_matches(".rs")))
        .collect::<Vec<_>>()
        .join("\n");

    std::fs::write(&mod_file, mod_content).unwrap();
}
