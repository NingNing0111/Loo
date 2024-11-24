package me.pgthinker.service.impl;

import lombok.RequiredArgsConstructor;
import me.pgthinker.admin.common.AdminConstants;
import me.pgthinker.mapper.ServerInfoMapper;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.service.ServerInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

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
}
