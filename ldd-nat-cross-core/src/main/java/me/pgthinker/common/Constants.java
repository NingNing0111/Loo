package me.pgthinker.common;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @Project: me.pgthinker.common
 * @Author: De Ning
 * @Date: 2024/10/7 19:18
 * @Description:
 */
public interface Constants {
    /**
     * 认证密码
     */
    String AUTH_PASSWORD = "auth_password";
    /**
     * 内网代理地址
     */
    String PROXY_HOST = "proxy_host";
    /**
     * 内网代理端口
     */
    String PROXY_PORT = "proxy_port";
    /**
     * 内网代理协议
     */
    String PROXY_PROTOCOL = "proxy_protocol";
    /**
     * 对外暴露的开放端口
     */
    String OPEN_PORT = "open_port";
    /**
     * 授权码
     */
    String LICENSE_KEY = "license_key";
    /**
     * 用户的访问ID
     *
     *        |--id-1----\
     * user   |--id-2-------> server
     *        |--id-3----/
     */
    String VISITOR_ID = "visitor_id";

    String MESSAGE = "message";
}
