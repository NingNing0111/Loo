package me.pgthinker.admin;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.vo.HeartbeatDataVO;
import me.pgthinker.admin.vo.RegisterServerVO;
import me.pgthinker.admin.vo.ServerClientVO;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.config.ServerConfig;
import me.pgthinker.core.manager.ServerManager;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Project: me.pgthinker.admin
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/2 18:36
 * @Description:
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AdminClient implements IAdminClient {
    private final RestTemplate restTemplate;
    private final ServerConfig serverConfig;
    private final ServerManager serverManager;
    @Getter
    @Setter
    private String serverId;

    @Override
    public String register() {
        String registerUrl = String.format("%s/admin/register", serverConfig.getAdmin().getBaseUrl());
        RegisterServerVO registerServerVO = new RegisterServerVO(serverConfig.getAdmin().getServerName(), serverConfig.getAdmin().getServerHostname(), serverConfig.getPort());
        String serverId = null;
        try {
            log.info("register Url:{} ,request body:{}", registerUrl, registerServerVO);
            BaseResponse baseResponse = restTemplate.postForObject(registerUrl, registerServerVO, BaseResponse.class);
            if (baseResponse != null && baseResponse.getCode() == 0 && baseResponse.getData() != null) {
                serverId = baseResponse.getData().toString();
                log.info("Register server successful [server id]:{}", serverId);
            }
        }catch (Exception e){
            log.error("Register server failed", e);
        }
        return serverId;

    }

    @Override
    public void startHeartbeat(String serverId) {
        String heartbeatUrl = String.format("%s/admin/heartbeat/%s", serverConfig.getAdmin().getBaseUrl(), serverId);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HeartbeatDataVO heartbeatDataVO = new HeartbeatDataVO();
                log.debug("heartbeat data:{}", heartbeatDataVO);
                try {
                    BaseResponse baseResponse = restTemplate.postForObject(heartbeatUrl, heartbeatDataVO, BaseResponse.class);
                    log.debug("心跳响应 {}", baseResponse);
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        },0, 10000);
    }

    @Override
    public VisitorConfigVO readVisitorList() {
        String visitorUrl = String.format("%s/admin/config/visitor/%s", serverConfig.getAdmin().getBaseUrl(), serverConfig.getAdmin().getServerName());
        VisitorConfigVO visitorConfig = null;
        try {
            BaseResponse res = restTemplate.getForObject(visitorUrl, BaseResponse.class);
            if(res != null && res.getCode() == 0 && res.getData() != null) {
                visitorConfig = (VisitorConfigVO) res.getData();
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return visitorConfig;
    }

    @Override
    public void addClientInfo (ServerClientVO serverClientVO) {
        String addClientInfoUrl = String.format("%s/admin/client/add", serverConfig.getAdmin().getBaseUrl());
        try {
            log.debug("client info:{}", serverClientVO);
            BaseResponse baseResponse = restTemplate.postForObject(addClientInfoUrl, serverClientVO, BaseResponse.class);
            log.debug("send client info result: {}", baseResponse);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Override
    public void removeClientInfo(String licenseKey) {
        String removeClientInfoUrl = String.format("%s/admin/client/delete/%s", serverConfig.getAdmin().getBaseUrl(), licenseKey);
        try {
            BaseResponse baseResponse = restTemplate.postForObject(removeClientInfoUrl, null , BaseResponse.class);
            log.debug("delete client info result: {}", baseResponse);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Override
    public void startUpdateClient(String serverId) {
        String updateClientUrl = String.format("%s/admin/client/offline/%s", serverConfig.getAdmin().getBaseUrl(), serverId);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    BaseResponse baseResponse = restTemplate.getForObject(updateClientUrl, BaseResponse.class);
                    log.info("base response:{}", baseResponse);
                    if(baseResponse != null && baseResponse.getCode() == 0 && baseResponse.getData() != null) {
                        List<String> licenseKeys = (List<String>) baseResponse.getData();
                        log.debug("license keys: {}", licenseKeys);
                        licenseKeys.forEach(licenseKey -> {
                            ChannelHandlerContext clientChannelCtx = serverManager.getClientChannelCtx(licenseKey);
                            clientChannelCtx.close();
                            log.debug("license [{}] -> close client channel ctx:{} ", licenseKey ,clientChannelCtx);
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
        },0, 5000);
    }


}
