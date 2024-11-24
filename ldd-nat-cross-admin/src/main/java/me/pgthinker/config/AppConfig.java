package me.pgthinker.config;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: me.pgthinker.config
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 22:54
 * @Description:
 */
@Configuration
public class AppConfig {
    @Bean(value = "boss")
    public NioEventLoopGroup boss(){
        return new NioEventLoopGroup(1);
    }

    @Bean(value = "worker")
    public NioEventLoopGroup worker(){
        return new NioEventLoopGroup();
    }

}
