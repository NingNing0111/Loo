package me.pgthinker.admin.service;

import me.pgthinker.admin.HeartbeatDataVO;
import me.pgthinker.admin.RegisterServerVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.admin.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/2 14:40
 * @Description:
 */
public interface AdminService {

    String register(RegisterServerVO registerServerVO);

    String heartbeat(String serverId, HeartbeatDataVO dataVO);

    Map<String, LocalDateTime> lastSystemInfo();

    void markServerAsDead(String serverName);

    void cleanServer();

    List<String> liveServer();
}
