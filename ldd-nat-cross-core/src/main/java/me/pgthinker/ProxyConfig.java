package me.pgthinker;

import lombok.Data;
import me.pgthinker.common.Constants;
import me.pgthinker.enums.ProtocolEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @Project: me.pgthinker.config
 * @Author: De Ning
 * @Date: 2024/10/7 18:44
 * @Description:
 */
@Data
public class ProxyConfig {
    /**
     * 内网需要代理的Host
     */
    private String host;
    /**
     * 内网需要代理的Host端口
     */
    private Integer port;
    /**
     * 内网需要代理的协议
     */
    private String protocol;
    /**
     * 代理后需要在服务端映射的公网端口
     */
    private Integer openPort;

    public Map<String, String> toMap(){
        Map<String, String> data = new HashMap<>();
        data.put(Constants.PROXY_HOST, this.host);
        data.put(Constants.PROXY_PORT, this.port.toString());
        data.put(Constants.PROXY_PROTOCOL, this.protocol);
        data.put(Constants.OPEN_PORT, this.openPort.toString());
        return data;
    }

    public static ProxyConfig fromMap(Map<String,String> data) {
        String host = data.get(Constants.PROXY_HOST);
        String port = data.get(Constants.PROXY_PORT);
        String protocol = data.get(Constants.PROXY_PROTOCOL);
        String openPort = data.get(Constants.OPEN_PORT);

        ProxyConfig proxyConfig = new ProxyConfig();
        proxyConfig.setHost(host);
        proxyConfig.setPort(Integer.parseInt(port));
        proxyConfig.setProtocol(protocol);
        proxyConfig.setOpenPort(Integer.parseInt(openPort));
        return proxyConfig;
    }
}
