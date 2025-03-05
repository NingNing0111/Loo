package me.pgthinker.service;

import me.pgthinker.model.entity.ServerClientDO;

import java.util.List;

/**
 * @Project: me.pgthinker.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/5 18:33
 * @Description:
 */
public interface ServerClientService {

    /**
     * 服务的客户端列表
     * @param serverId
     * @return
     */
    List<ServerClientDO> clientList(String serverId);

}
