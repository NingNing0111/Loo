package me.pgthinker.admin.service;

import me.pgthinker.admin.vo.HeartbeatDataVO;
import me.pgthinker.admin.vo.RegisterServerVO;
import me.pgthinker.admin.vo.ServerClientVO;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.model.entity.VisitorConfigDO;

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

    /**
     * 注册服务
     * @param registerServerVO
     * @return
     */
    String register(RegisterServerVO registerServerVO);

    /**
     * 心跳信息
     * @param serverId
     * @param dataVO
     * @return
     */
    String heartbeat(String serverId, HeartbeatDataVO dataVO);

    /**
     * 最新的系统信息
     * @return
     */
    Map<String, LocalDateTime> lastSystemInfo();

    /**
     * 标记服务断开
     * @param serverName
     */
    void markServerAsDead(String serverName);

    /**
     * 清理超时的服务
     */
    void cleanServer();

    /**
     * 在线的服务列表
     * @return
     */
    List<String> liveServer();

    /**
     * 添加服务端的客户端信息
     * @param serverClientVO
     */
    void addClient(ServerClientVO serverClientVO);

    /**
     * 授权码
     * @param licenseKey
     */
    void removeClient(String licenseKey);

    /**
     * 获取服务端访问控制配置列表
     * @return
     */
    VisitorConfigVO visitorConfig(String serverName);
}
