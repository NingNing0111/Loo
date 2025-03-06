package me.pgthinker.server;

import io.netty.channel.nio.NioEventLoopGroup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.AdminClient;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.config.AdminConfig;
import me.pgthinker.config.ServerConfig;
import me.pgthinker.core.initializer.ServerInitializer;
import me.pgthinker.net.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
    private final AdminClient adminClient;

    @Resource(name = "boss")
    private NioEventLoopGroup boss;
    @Resource(name = "worker")
    private NioEventLoopGroup worker;

    /**
     * 初始化代理服务
     */
    @PostConstruct
    public void initProxyServer(){
        AdminConfig admin = serverConfig.getAdmin();

        if(admin.getEnabled()) {
            String serverId = adminClient.register();
            if(serverId == null) {
                log.error("admin register failed");
                return;
            }
            adminClient.setServerId(serverId);
            VisitorConfigVO visitorConfigVO = adminClient.readVisitorList();
            if(visitorConfigVO != null) {
                List<String> blackList = visitorConfigVO.getBlackList();
                List<String> whiteList = visitorConfigVO.getWhiteList();
                serverConfig.setBlackList(blackList);
                serverConfig.setWhiteList(whiteList);
            }
            adminClient.startHeartbeat(serverId);
            adminClient.startUpdateClient(serverId);
        }
        startServer();
    }

    private void startServer() {
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
