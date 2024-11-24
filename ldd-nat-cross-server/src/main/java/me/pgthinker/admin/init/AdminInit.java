package me.pgthinker.admin.init;

import io.netty.channel.nio.NioEventLoopGroup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.core.initializer.AdminInitializer;
import me.pgthinker.config.AdminConfig;
import me.pgthinker.net.TcpConnect;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Project: me.pgthinker.admin.init
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 18:53
 * @Description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInit {

    private final AdminConfig adminConfig;

    @Resource(name = "admin")
    private NioEventLoopGroup admin;

    @PostConstruct
    public void init() {
        Boolean enabled = adminConfig.getEnabled();
        if(!enabled) return;
        String hostname = adminConfig.getHostname();
        Integer port = adminConfig.getPort();
        String serverName = adminConfig.getServerName();

        try {
            TcpConnect tcpConnect = new TcpConnect(admin);
            tcpConnect.connect(hostname,port,new AdminInitializer(serverName));
        }catch (Exception e){
            log.error("Unexpected error during admin initialization:{}", e.getMessage());
        }

    }
}
