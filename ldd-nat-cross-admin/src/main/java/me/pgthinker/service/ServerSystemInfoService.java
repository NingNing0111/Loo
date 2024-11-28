package me.pgthinker.service;

import me.pgthinker.model.entity.ServerSystemInfoDO;
import me.pgthinker.model.vo.ServerSystemReqVO;

import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:47
 * @Description:
 */
public interface ServerSystemInfoService {

    void addSystemInfo(String serverId, Map<String, String> systemInfo);


    List<ServerSystemInfoDO> list(ServerSystemReqVO reqVO);

}
