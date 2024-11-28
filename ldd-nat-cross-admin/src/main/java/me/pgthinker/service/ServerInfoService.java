package me.pgthinker.service;

import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.vo.ServerInfoVO;

import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:47
 * @Description:
 */
public interface ServerInfoService {

    void addServerInfo(String serverId, Map<String, String> serverInfo);

    List<ServerInfoVO> list();


}
