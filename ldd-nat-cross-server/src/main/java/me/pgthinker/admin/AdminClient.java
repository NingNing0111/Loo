package me.pgthinker.admin;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.common.BaseResponse;
import me.pgthinker.common.ResultUtils;
import me.pgthinker.config.AdminConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.rmi.registry.Registry;
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
        String registerUrl = String.format("http://%s:%s/api/admin/register", adminConfig.getHostname(), adminConfig.getPort());
        String serverHostName = "";
        if (StringUtils.isNotEmpty(adminConfig.getServerHostname())) {
            serverHostName = adminConfig.getServerHostname();
        }else{
            serverHostName = adminConfig.defaultServerHostname();
        }
        RegisterServerVO registerServerVO = new RegisterServerVO(adminConfig.getServerName(), serverHostName);
        try {
            log.info("request body:{}", registerServerVO);
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
        String heartbeatUrl = String.format("http://%s:%s/api/admin/heartbeat/%s", adminConfig.getHostname(), adminConfig.getPort(), serverId);

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                HeartbeatDataVO heartbeatDataVO = new HeartbeatDataVO();
                log.info("心跳：{}", heartbeatDataVO);
                try {

                    BaseResponse baseResponse = restTemplate.postForObject(heartbeatUrl, heartbeatDataVO, BaseResponse.class);
                    log.info("心跳响应 {}", baseResponse);
                }catch (Exception e){
                    log.info(e.getMessage());
                }
            }
        },0, 5000);
    }


}
