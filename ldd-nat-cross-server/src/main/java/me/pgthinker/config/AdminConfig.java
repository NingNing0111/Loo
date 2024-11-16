package me.pgthinker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: me.pgthinker.config
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/14 22:06
 * @Description:
 */
@Configuration
@ConfigurationProperties(prefix = "admin")
@Data
public class AdminConfig {
    private Boolean enabled;
    private String hostname;
    private Integer port;
    private String serverName;
}
