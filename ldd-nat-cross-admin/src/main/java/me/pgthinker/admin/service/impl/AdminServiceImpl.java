package me.pgthinker.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.HeartbeatDataVO;
import me.pgthinker.admin.RegisterServerVO;
import me.pgthinker.admin.service.AdminService;
import me.pgthinker.mapper.ServerInfoMapper;
import me.pgthinker.mapper.ServerSystemInfoMapper;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.entity.ServerSystemInfoDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.admin.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/2 17:51
 * @Description:
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final static Long TIMEOUT = 30000L;

    private final ServerInfoMapper serverInfoMapper;
    private final ServerSystemInfoMapper systemInfoMapper;

    // 注册失败返回null
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String register(RegisterServerVO registerServerVO) {
        log.info("开始注册信息:{}", registerServerVO);
        // 清理下服务
        this.cleanServer();
        String serverName = registerServerVO.getServerName();
        // 判断服务名是否已经存在
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ServerInfoDO::getServerName, serverName);
        qw.eq(ServerInfoDO::getIsLive, true);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);
        // 已经存在了 不允许注册
        if (!serverInfoDOS.isEmpty()) {
            return null;
        }
        ServerInfoDO serverInfoDO = new ServerInfoDO();
        serverInfoDO.setServerName(registerServerVO.getServerName());
        serverInfoDO.setIsLive(true);
        serverInfoDO.setRegisterTime(LocalDateTime.now());
        serverInfoDO.setHostname(registerServerVO.getHostname());
        serverInfoDO.setOsName(registerServerVO.getOsName());
        serverInfoDO.setOsVersion(registerServerVO.getOsVersion());
        serverInfoDO.setOsArch(registerServerVO.getOsArch());
        log.info("插入的实体类:{}", serverInfoDO);
        serverInfoMapper.insert(serverInfoDO);
        return serverInfoDO.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String heartbeat(String serverId, HeartbeatDataVO dataVO) {
        log.info("收到来自:{} 的心跳信息:{}", serverId, dataVO);
        ServerSystemInfoDO serverSystemInfoDO = new ServerSystemInfoDO();
        BeanUtils.copyProperties(dataVO, serverSystemInfoDO);
        serverSystemInfoDO.setServerId(serverId);
        serverSystemInfoDO.setRegisterTime(LocalDateTime.now());
        systemInfoMapper.insert(serverSystemInfoDO);
        return "ok";
    }

    @Override
    public Map<String, LocalDateTime> lastSystemInfo() {
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ServerInfoDO::getIsLive, true);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);

        List<String> serverIds = serverInfoDOS.stream().map(ServerInfoDO::getId).toList();
        LambdaQueryWrapper<ServerSystemInfoDO> systemInfoQW = new LambdaQueryWrapper<>();
        systemInfoQW.in(ServerSystemInfoDO::getServerId, serverIds);
        List<ServerSystemInfoDO> serverSystemInfoDOS = systemInfoMapper.selectList(systemInfoQW);
        Map<String, LocalDateTime> res = new HashMap<>();
        for (ServerSystemInfoDO serverSystemInfoDO : serverSystemInfoDOS) {
            if (!res.containsKey(serverSystemInfoDO.getServerId())) {
                res.put(serverSystemInfoDO.getServerId(), serverSystemInfoDO.getRegisterTime());
            }else{
                // 已存储的时间
                LocalDateTime localDateTime = res.get(serverSystemInfoDO.getServerId());
                // 当前时间
                if (localDateTime.isBefore(serverSystemInfoDO.getRegisterTime())) {
                    res.put(serverSystemInfoDO.getServerId(), serverSystemInfoDO.getRegisterTime());
                }
            }

        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void markServerAsDead(String serverName) {
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ServerInfoDO::getServerName, serverName);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);
        serverInfoDOS.forEach(item -> {
            item.setIsLive(false);
        });
        serverInfoMapper.updateById(serverInfoDOS);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cleanServer() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, LocalDateTime> stringLocalDateTimeMap = this.lastSystemInfo();
        log.info("lastSystemInfo:{}", stringLocalDateTimeMap);
        if (stringLocalDateTimeMap.keySet().isEmpty()) {
            List<String> strings = this.liveServer();
            for (String serverId : strings) {
                this.markServerAsDead(serverId);
            }
        }
        for (String serverId : stringLocalDateTimeMap.keySet()) {
            LocalDateTime lastRegisterTime = stringLocalDateTimeMap.get(serverId);
            // 判断serverLastRegisterTime 和 now 是否相差TIMEOUT
            long diffMillis = Duration.between(lastRegisterTime, now).toMillis();

            if (diffMillis > TIMEOUT) {
                log.info("清理服务:{}", serverId);
                this.markServerAsDead(serverId); // 假设该方法设置 isLive = false
            }
        }
    }

    @Override
    public List<String> liveServer() {
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ServerInfoDO::getIsLive, true);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);
        return serverInfoDOS.stream().map(ServerInfoDO::getServerName).toList();
    }
}
