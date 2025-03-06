package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.mapper.ServerInfoMapper;
import me.pgthinker.mapper.ServerSystemInfoMapper;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.entity.ServerSystemInfoDO;
import me.pgthinker.model.vo.AnalysisDataVO;
import me.pgthinker.model.vo.ServerSystemReqVO;
import me.pgthinker.model.vo.SystemInfoVO;
import me.pgthinker.service.ServerSystemInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    private final ServerInfoMapper serverInfoMapper;

    @Override
    public List<ServerSystemInfoDO> list(ServerSystemReqVO reqVO) {
        LambdaQueryWrapper<ServerSystemInfoDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ServerSystemInfoDO::getServerId, reqVO.getServerId());
        return serverSystemInfoMapper.selectList(qw);
    }

    @Override
    public List<AnalysisDataVO> analysisData(String serverName, String timeType) {
        LambdaQueryWrapper<ServerInfoDO> serverInfoQW = new LambdaQueryWrapper<>();
        serverInfoQW.eq(ServerInfoDO::getServerName, serverName);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(serverInfoQW);
        List<String> serverIds = serverInfoDOS.stream().map(ServerInfoDO::getId).toList();
        if(serverIds.isEmpty()) {
            return List.of();
        }
        if(Objects.equals("day", timeType)) {
            return serverSystemInfoMapper.inDaySystemInfoList(serverName);
        }
        if(Objects.equals("month", timeType)) {
            return serverSystemInfoMapper.onMonthSystemInfoList(serverName);
        }
        return List.of();

    }

    @Override
    public AnalysisDataVO lastSystemData(String serverName) {
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ServerInfoDO::getServerName, serverName);
        qw.eq(ServerInfoDO::getIsLive, true);
        qw.orderByDesc(ServerInfoDO::getRegisterTime);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);

        if (ObjectUtils.isEmpty(serverInfoDOS)) {
            return null;
        }
        ServerInfoDO serverInfoDO = serverInfoDOS.get(0);
        LambdaQueryWrapper<ServerSystemInfoDO> systemQW = new LambdaQueryWrapper<>();
        systemQW.eq(ServerSystemInfoDO::getServerId, serverInfoDO.getId());
        systemQW.orderByDesc(ServerSystemInfoDO::getRegisterTime);
        List<ServerSystemInfoDO> systemInfos = serverSystemInfoMapper.selectList(systemQW);
        if (ObjectUtils.isEmpty(systemInfos)) {
            return null;
        }
        ServerSystemInfoDO serverSystemInfoDO = systemInfos.get(0);

        return transform2AnalysisDataVO(serverSystemInfoDO);
    }

    private List<SystemInfoVO> transform(List<ServerSystemInfoDO> systemInfoDOS) {
        List<SystemInfoVO> res = systemInfoDOS.stream().map(item -> {
            SystemInfoVO systemInfoVO = new SystemInfoVO();
            BeanUtils.copyProperties(item, systemInfoVO);
            return systemInfoVO;
        }).toList();
        return res;
    }

    private AnalysisDataVO transform2AnalysisDataVO(ServerSystemInfoDO serverSystemInfoDO) {
        AnalysisDataVO analysisDataVO = new AnalysisDataVO();
        BeanUtils.copyProperties(serverSystemInfoDO, analysisDataVO);
        return analysisDataVO;
    }
}
