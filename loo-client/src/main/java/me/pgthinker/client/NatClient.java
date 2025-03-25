package me.pgthinker.client;

import io.netty.channel.nio.NioEventLoopGroup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.config.ClientConfig;
import me.pgthinker.core.initializer.ClientInitializer;
import me.pgthinker.net.tcp.TcpConnect;
import org.springframework.stereotype.Component;

/**
 * @Project: me.pgthinker.client
 * @Author: De Ning
 * @Date: 2024/10/29 11:29
 * @Description:
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NatClient {

    private final ClientConfig clientConfig;
    @Resource(name = "worker")
    private final NioEventLoopGroup worker;
    @PostConstruct
    public void init() {
        try {
            TcpConnect tcpConnect = new TcpConnect(worker);
            ClientInitializer clientInitializer = new ClientInitializer(clientConfig);
            tcpConnect.connect(clientConfig.getServerHost(), clientConfig.getServerPort(), clientInitializer);
        } catch (Exception e) {
            log.error("Unexpected error during client initialization: {}", e.getMessage());
        }

    }
}
