package me.pgthinker.admin;

import io.netty.channel.nio.NioEventLoopGroup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.config.AdminConfig;
import me.pgthinker.core.initializer.AdminInitializer;
import me.pgthinker.net.TcpServer;
import org.springframework.stereotype.Component;

/**
 * @Project: me.pgthinker.admin
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 22:53
 * @Description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminServerInit {
    private final AdminConfig adminConfig;
    @Resource(name = "boss")
    private NioEventLoopGroup boss;
    @Resource(name = "worker")
    private NioEventLoopGroup worker;

    @PostConstruct
    public void initAdminServer() {
        TcpServer tcpServer = new TcpServer(boss, worker);
        try {
            tcpServer.bind(adminConfig.getPort(), new AdminInitializer());
        }catch (InterruptedException e){
            log.error("admin server start fail. err msg:{}", e.getMessage());
        }
        log.info("Admin system run successful!");
    }
}
