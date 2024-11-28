package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.common.AdminConstants;
import me.pgthinker.mapper.ServerSystemInfoMapper;
import me.pgthinker.model.entity.ServerSystemInfoDO;
import me.pgthinker.model.vo.ServerSystemReqVO;
import me.pgthinker.service.ServerSystemInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.service.impl
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:52
 * @Description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServerSystemInfoServiceImpl implements ServerSystemInfoService {

    private final ServerSystemInfoMapper serverSystemInfoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addSystemInfo(String serverId, Map<String, String> systemInfo) {
        String freeMemory = systemInfo.get(AdminConstants.RUNTIME_FREE_MEMORY);
        String maxMemory = systemInfo.get(AdminConstants.RUNTIME_MAX_MEMORY);
        String totalMemory = systemInfo.get(AdminConstants.RUNTIME_TOTAL_MEMORY);
        String usableMemory = systemInfo.get(AdminConstants.RUNTIME_USABLE_MEMORY);
        ServerSystemInfoDO serverSystemInfoDO = new ServerSystemInfoDO();
        serverSystemInfoDO.setServerId(serverId);
        serverSystemInfoDO.setFreeMemory(Long.parseLong(freeMemory));
        serverSystemInfoDO.setMaxMemory(Long.parseLong(maxMemory));
        serverSystemInfoDO.setTotalMemory(Long.parseLong(totalMemory));
        serverSystemInfoDO.setUsableMemory(Long.parseLong(usableMemory));
        serverSystemInfoDO.setRegisterTime(LocalDateTime.now());
        serverSystemInfoMapper.insert(serverSystemInfoDO);
    }

    @Override
    public List<ServerSystemInfoDO> list(ServerSystemReqVO reqVO) {

        LambdaQueryWrapper<ServerSystemInfoDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ServerSystemInfoDO::getServerId, reqVO.getServerId());
        return serverSystemInfoMapper.selectList(qw);
    }
}
