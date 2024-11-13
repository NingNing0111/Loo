package me.pgthinker.config;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: me.pgthinker.config
 * @Author: De Ning
 * @Date: 2024/10/29 13:40
 * @Description:
 */
@Configuration
public class AppConfig {

    @Bean(value = "worker")
    public NioEventLoopGroup worker(){
        return new NioEventLoopGroup();
    }
}
