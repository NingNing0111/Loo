package me.pgthinker.service;

import me.pgthinker.model.entity.ServerSystemInfoDO;
import me.pgthinker.model.vo.ServerInfoVO;
import me.pgthinker.model.vo.ServerSystemReqVO;
import me.pgthinker.model.vo.SystemInfoVO;

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


    List<ServerSystemInfoDO> list(ServerSystemReqVO reqVO);

    List<SystemInfoVO> analysisData(String serverName);


}
