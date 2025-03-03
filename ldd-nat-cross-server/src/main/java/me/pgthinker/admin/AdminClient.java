package me.pgthinker.admin;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.config.AdminConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Project: me.pgthinker.admin
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/2 18:36
 * @Description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminClient {
    private final AdminConfig adminConfig;
    private final RestTemplate restTemplate;
    private String serverId = "";

    @PostConstruct
    public void register() {
        if (!adminConfig.getEnabled()) {
            return;
        }
        String registerUrl = String.format("%s/admin/register", adminConfig.getBaseUrl());
        String serverHostName = "";
        if (StringUtils.isNotEmpty(adminConfig.getServerHostname())) {
            serverHostName = adminConfig.getServerHostname();
        }else{
            serverHostName = adminConfig.defaultServerHostname();
        }
        RegisterServerVO registerServerVO = new RegisterServerVO(adminConfig.getServerName(), serverHostName);
        try {
            log.info("register Url:{} ,request body:{}", registerUrl, registerServerVO);
            BaseResponse baseResponse = restTemplate.postForObject(registerUrl, registerServerVO, BaseResponse.class);
            log.info("{}",baseResponse);
            if (baseResponse != null && baseResponse.getCode() == 0 && baseResponse.getData() != null) {
                this.serverId = baseResponse.getData().toString();
                sendHeartbeat(serverId);
            }
            log.info("Register server successful [server id]:{}", this.serverId);
        }catch (Exception e){
            log.error("Register server failed", e);
        }

    }

    private void sendHeartbeat(String serverId) {
        Timer timer = new Timer();
        String heartbeatUrl = String.format("%s/admin/heartbeat/%s", adminConfig.getBaseUrl(), serverId);

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                HeartbeatDataVO heartbeatDataVO = new HeartbeatDataVO();
                log.info("heartbeat data:{}", heartbeatDataVO);
                try {
                    BaseResponse baseResponse = restTemplate.postForObject(heartbeatUrl, heartbeatDataVO, BaseResponse.class);
                    log.debug("心跳响应 {}", baseResponse);
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        },0, 10000);
    }


}
