package me.pgthinker.config;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: me.pgthinker.config
 * @Author: De Ning
 * @Date: 2024/10/22 23:46
 * @Description:
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final ServerConfig serverConfig;

    @Bean(value = "boss")
    public NioEventLoopGroup boss(){
        return new NioEventLoopGroup(serverConfig.getBossCnt());
    }

    @Bean(value = "worker")
    public NioEventLoopGroup worker(){
        return new NioEventLoopGroup(serverConfig.getWorkerCnt());
    }
}
