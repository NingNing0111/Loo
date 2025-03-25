package me.pgthinker.admin.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.service.AdminService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Project: me.pgthinker.admin.job
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/2 17:59
 * @Description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CleanJob {
    private final AdminService adminService;
    /**
     * 定时清理超时 Server
     * 查询心跳数据 拿到最新且去重的 serverId-registerTime
     * 根据判断now - registerTime > TIMEOUT ，若true 则说明超时了，此时将serverId的isLive设置为false
     */
    @Scheduled(fixedRate = 10000)
    public void cleanUp() {
        log.info("开始清理服务...");
        adminService.cleanServer();
        List<String> strings = adminService.liveServer();
        log.info("Live server: {}", strings);
    }
}
