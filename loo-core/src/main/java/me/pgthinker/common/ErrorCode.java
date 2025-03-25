package me.pgthinker.common;

/**
 * @Project: me.pgthinker.common
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 01:23
 * @Description:
 */
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40333, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    UPDATE_ERROR(50002,"更新失败"),
    DELETE_ERROR(50004,"删除失败"),
    USER_NOT_FOUNT(5011,"用户不存在"),
    USER_ACCOUNT_ERROR(5012,"用户名或密码错误"),
    USER_EXISTS(5013,"用户已存在"),
    VISITOR_CONFIG_EXISTS(5014, "配置已存在"),

    REFRESH_TOKEN(40300,"令牌异常");


    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
