package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import me.pgthinker.admin.common.AdminConstants;
import me.pgthinker.core.Manager.AdminManager;
import me.pgthinker.mapper.ServerInfoMapper;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.vo.ServerInfoVO;
import me.pgthinker.service.ServerInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project: me.pgthinker.service.impl
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:49
 * @Description:
 */
@Service
@RequiredArgsConstructor
public class ServerInfoServiceImpl implements ServerInfoService {

    private final ServerInfoMapper serverInfoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addServerInfo(String serverId, Map<String, String> serverInfo) {
        String serverName = serverInfo.get(AdminConstants.SERVER_NAME);
        String osName = serverInfo.get(AdminConstants.OS_NAME);
        String osArch = serverInfo.get(AdminConstants.OS_ARCH);
        String osVersion = serverInfo.get(AdminConstants.OS_VERSION);
        ServerInfoDO serverInfoDO = new ServerInfoDO();
        serverInfoDO.setServerId(serverId);
        serverInfoDO.setServerName(serverName);
        serverInfoDO.setOsName(osName);
        serverInfoDO.setOsArch(osArch);
        serverInfoDO.setOsVersion(osVersion);
        serverInfoDO.setRegisterTime(LocalDateTime.now());
        serverInfoMapper.insert(serverInfoDO);
    }

    @Override
    public List<ServerInfoVO> list() {
        // cur
        List<String> curServerIds = AdminManager.serverIds();
        // all
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);

        return serverInfoDOS.stream().map(item -> {
            ServerInfoVO serverInfoVO = new ServerInfoVO();
            BeanUtils.copyProperties(item, serverInfoVO);
            serverInfoVO.setIsLive(curServerIds.contains(item.getServerId()));
            return serverInfoVO;
        }).sorted((o1, o2) -> {
            int a = o1.getIsLive() ? 1 : 0;
            int b = o2.getIsLive() ? 1 : 0;
            return b - a;
        }).collect(Collectors.toList());

    }
}
