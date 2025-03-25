package me.pgthinker.config;

import cn.hutool.system.SystemUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Project: me.pgthinker.config
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/14 22:06
 * @Description:
 */
@Configuration
@ConfigurationProperties(prefix = "server.admin")
@Data
public class AdminConfig {
    private Boolean enabled = false;
    private String baseUrl;
    private String serverName = SystemUtil.getUserInfo().getName();
    private String serverHostname;


    public String defaultServerHostname(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }catch (UnknownHostException e){
            return "UnknownHost";
        }
    }
}
