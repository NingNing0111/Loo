package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import me.pgthinker.admin.common.AdminConstants;
import me.pgthinker.core.Manager.AdminManager;
import me.pgthinker.mapper.ServerInfoMapper;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.vo.ServerInfoVO;
import me.pgthinker.service.ServerInfoService;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public void addServerInfo(ChannelHandlerContext ctx, Map<String, String> serverInfo) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();

        String serverName = serverInfo.get(AdminConstants.SERVER_NAME);
        String osName = serverInfo.get(AdminConstants.OS_NAME);
        String osArch = serverInfo.get(AdminConstants.OS_ARCH);
        String osVersion = serverInfo.get(AdminConstants.OS_VERSION);
        ServerInfoDO serverInfoDO = new ServerInfoDO();
        serverInfoDO.setServerId(ctx.channel().id().asLongText());
        serverInfoDO.setServerName(serverName);
        serverInfoDO.setOsName(osName);
        serverInfoDO.setOsArch(osArch);
        serverInfoDO.setOsVersion(osVersion);
        serverInfoDO.setRegisterTime(LocalDateTime.now());
        serverInfoDO.setIp(address.getAddress().getHostAddress());
        serverInfoDO.setHostname(address.getHostName());
        serverInfoDO.setPort(address.getPort());
        serverInfoMapper.insert(serverInfoDO);
    }

    @Override
    public List<ServerInfoVO> list() {
        // cur
        List<String> curServerIds = AdminManager.serverIds();
        // all
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(ServerInfoDO::getRegisterTime);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);

        List<ServerInfoVO> allServer = serverInfoDOS.stream().map(item -> {
            ServerInfoVO serverInfoVO = new ServerInfoVO();
            BeanUtils.copyProperties(item, serverInfoVO);
            serverInfoVO.setIsLive(curServerIds.contains(item.getServerId()));
            return serverInfoVO;
        }).sorted((o1, o2) -> {
            int a = o1.getIsLive() ? 1 : 0;
            int b = o2.getIsLive() ? 1 : 0;
            return b - a;
        }).toList();

        Map<String, List<ServerInfoVO>> collect = allServer.stream().collect(Collectors.groupingBy(ServerInfoVO::getServerName));
        ArrayList<ServerInfoVO> res = new ArrayList<>();
        for(List<ServerInfoVO> serverInfo:collect.values()) {
            res.add(serverInfo.get(0));
        }
        return res;
    }

    @Override
    public Page<ServerInfoVO> historyList(ServerInfoVO serverInfoVO, Integer page, Integer pageSize) {
        String serverName = serverInfoVO.getServerName();
        LambdaQueryWrapper<ServerInfoDO> serverQW = new LambdaQueryWrapper<>();
        serverQW.eq(ServerInfoDO::getServerName, serverName);
        Page<ServerInfoDO> qPage = Page.of(page, pageSize);

        Page<ServerInfoDO> serverInfoDOPage = serverInfoMapper.selectPage(qPage, serverQW);
        List<ServerInfoVO> vos = transform(serverInfoDOPage.getRecords());
        Page<ServerInfoVO> res = new Page<>();
        BeanUtils.copyProperties(serverInfoDOPage, res);
        res.setRecords(vos);
        return res;
    }

    private List<ServerInfoVO> transform(List<ServerInfoDO> serverInfoDOS) {
        return serverInfoDOS.stream().map(item -> {
            ServerInfoVO serverInfoVO = new ServerInfoVO();
            BeanUtils.copyProperties(item, serverInfoVO);
            return serverInfoVO;
        }).collect(Collectors.toList());
    }

}
