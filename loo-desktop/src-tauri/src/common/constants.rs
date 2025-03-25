/**
 * 认证密码
 */
pub const AUTH_PASSWORD: &str = "auth_password";
/**
 * 代理主机
 */
pub const PROXY_HOST: &str = "proxy_host";
/**
 * 代理端口
 */
pub const PROXY_PORT: &str = "proxy_port";
/**
 * 代理协议
 */
pub const PROXY_PROTOCOL: &str = "proxy_protocol";
/**
 * 开放端口
 */
pub const OPEN_PORT: &str = "open_port";
/**
 * 客户端授权码
 */
pub const LICENSE_KEY: &str = "license_key";
/**
 * 用户访问隧道Id
 */
pub const VISITOR_ID: &str = "visitor_id";
/**
 * 消息
 */
pub const MESSAGE: &str = "message";

/**
 * 服务端处的错误代码
 */
pub const SERVER_ERROR_CODE: i32 = 10;

/**
 * 客户端处的错误代码
 */
pub const APP_ERROR_CODE: i32 = 100;

/**
 * 日志操作
 */
pub const APP_CONNECT: i32 = 0; // APP连接
pub const APP_DISCONNECT: i32 = 1; // APP端口
pub const CONFIG_QUERY: i32 = 2; // 配置查询
pub const CONFIG_ADD: i32 = 3; // 配置添加
pub const CONFIG_UPDATE: i32 = 4; // 配置更新
pub const CONFIG_DELETE: i32 = 5; // 配置删除
pub const SETTING_UPDATE: i32 = 6; // 系统设置更新
pub const APP_RUNTIME: i32 = 7; // APP执行过程中

/**
 * 日志类型
 */
pub const NORMAL: i32 = 0; // 一般日志/正常日志
pub const ERR: i32 = 1; // 错误日志
