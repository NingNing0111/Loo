package me.pgthinker.server;

import io.netty.channel.nio.NioEventLoopGroup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.config.AppConfig;
import me.pgthinker.config.ServerConfig;
import me.pgthinker.core.initializer.ServerInitializer;
import me.pgthinker.net.TcpServer;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * @Project: me.pgthinker.server
 * @Author: De Ning
 * @Date: 2024/10/26 18:22
 * @Description:
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProxyServer {

    private final ServerConfig serverConfig;

    @Resource(name = "boss")
    private NioEventLoopGroup boss;
    @Resource(name = "worker")
    private NioEventLoopGroup worker;

    /**
     * 初始化代理服务
     */
    @PostConstruct
    public void initProxyServer(){
        TcpServer tcpServer = new TcpServer(boss, worker);
        try {
            tcpServer.bind(serverConfig.getPort(), new ServerInitializer(tcpServer));
            printSuccessStarted();
        }catch (InterruptedException e){
            log.error("server start fail. error msg:{}", e.getMessage());
        }
    }


    private void printSuccessStarted(){
        String banner = """
                \n===============================================
                \n The server started successfully!  Port: [{}]  
                \n===============================================
                """;
        log.info(banner, serverConfig.getPort());
    }
}
