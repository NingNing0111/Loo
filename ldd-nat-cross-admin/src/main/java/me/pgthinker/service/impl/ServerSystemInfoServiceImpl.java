package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.common.AdminConstants;
import me.pgthinker.mapper.ServerInfoMapper;
import me.pgthinker.mapper.ServerSystemInfoMapper;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.entity.ServerSystemInfoDO;
import me.pgthinker.model.vo.ServerInfoVO;
import me.pgthinker.model.vo.ServerSystemReqVO;
import me.pgthinker.model.vo.SystemInfoVO;
import me.pgthinker.service.ServerSystemInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<SystemInfoVO> analysisData(String serverName) {
        LambdaQueryWrapper<ServerInfoDO> serverInfoQW = new LambdaQueryWrapper<>();
        serverInfoQW.eq(ServerInfoDO::getServerName, serverName);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(serverInfoQW);
        List<String> serverIds = serverInfoDOS.stream().map(ServerInfoDO::getId).toList();
        if (!serverIds.isEmpty()) {
            LambdaQueryWrapper<ServerSystemInfoDO> systemInfoQW = new LambdaQueryWrapper<>();
            systemInfoQW.in(ServerSystemInfoDO::getServerId, serverIds);
            systemInfoQW.orderByDesc(ServerSystemInfoDO::getRegisterTime);
            // 限定100条数据
            systemInfoQW.last("LIMIT 500"); // 限制最多返回 100 条数据
            List<ServerSystemInfoDO> res = serverSystemInfoMapper.selectList(systemInfoQW);
            // 反转
            Collections.reverse(res);
            return transform(res);
        }

        return List.of();
    }

    private List<SystemInfoVO> transform(List<ServerSystemInfoDO> systemInfoDOS) {
        List<SystemInfoVO> res = systemInfoDOS.stream().map(item -> {
            SystemInfoVO systemInfoVO = new SystemInfoVO();
            BeanUtils.copyProperties(item, systemInfoVO);
            return systemInfoVO;
        }).toList();
        return res;
    }
}
