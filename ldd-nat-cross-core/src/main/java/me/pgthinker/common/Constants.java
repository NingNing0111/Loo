package me.pgthinker.common;

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
     * 客户端ID
     *        |--id-1--- client1
     * server |--id-2--- client2
     *        |--id-3--- client3
     *
     */
    String CLIENT_ID = "client_id";
    /**
     * 内网的隧道ID
     *
     *        |--id-1---- mysql
     * client |--id-2---- redis
     *        |--id-3---- minio
     */
    String CLIENT_CHANNEL_ID = "client_channel_id";
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
