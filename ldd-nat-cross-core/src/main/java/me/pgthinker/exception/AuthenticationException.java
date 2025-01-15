package me.pgthinker.exception;

/**
 * @Project: me.pgthinker.exception
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/13 10:13
 * @Description:
 */
public class AuthenticationException extends RuntimeException{
    public AuthenticationException() {
        super("AuthenticationException: Failed to authenticate from server.");
    }

    public AuthenticationException(String ip, Integer port) {
        super("AuthenticationException: Failed to authenticate from server. Server info:[" + ip + "/" + port + "]");
    }
}
