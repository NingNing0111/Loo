package me.pgthinker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: me.pgthinker.config
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 22:51
 * @Description:
 */
@ConfigurationProperties(prefix = "admin")
@Configuration
@Data
public class AdminConfig {
    private Integer port;
}
