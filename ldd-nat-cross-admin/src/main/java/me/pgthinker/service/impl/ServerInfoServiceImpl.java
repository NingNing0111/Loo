package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.pgthinker.mapper.ServerClientMapper;
import me.pgthinker.mapper.ServerInfoMapper;
import me.pgthinker.model.entity.ServerClientDO;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.vo.ServerInfoVO;
import me.pgthinker.model.vo.SimpleServerVO;
import me.pgthinker.service.ServerInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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
    private final ServerClientMapper serverClientMapper;

    @Override
    public List<ServerInfoVO> list() {
        List<ServerInfoVO> res = new ArrayList<>();
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(ServerInfoDO::getIsLive);
        qw.orderByDesc(ServerInfoDO::getRegisterTime);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);
        Map<String, List<ServerInfoDO>> groupList = serverInfoDOS.stream().collect(Collectors.groupingBy(ServerInfoDO::getServerName));
        for (String key : groupList.keySet()) {
            List<ServerInfoDO> groupServers = groupList.get(key);
            List<ServerInfoVO> handledServerInfoVOs = transform(groupServers);
            res.add(handledServerInfoVOs.get(0));
        }
        return res;
    }

    @Override
    public Page<ServerInfoVO> historyList(ServerInfoVO serverInfoVO) {
        String serverName = serverInfoVO.getServerName();
        LambdaQueryWrapper<ServerInfoDO> serverQW = new LambdaQueryWrapper<>();
        serverQW.eq(ServerInfoDO::getServerName, serverName);
        serverQW.orderByDesc(ServerInfoDO::getRegisterTime);
        Page<ServerInfoDO> qPage = Page.of(serverInfoVO.getPage(), serverInfoVO.getPageSize());

        Page<ServerInfoDO> serverInfoDOPage = serverInfoMapper.selectPage(qPage, serverQW);
        List<ServerInfoVO> vos = transform(serverInfoDOPage.getRecords());
        Page<ServerInfoVO> res = new Page<>();
        BeanUtils.copyProperties(serverInfoDOPage, res);
        res.setRecords(vos);
        return res;
    }

    @Override
    public List<SimpleServerVO> simpleList() {
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);
        List<String> serverName = serverInfoDOS.stream().map(ServerInfoDO::getServerName).collect(Collectors.toSet()).stream().toList();
        return transform2Simple(serverName);
    }

    private List<ServerInfoVO> transform(List<ServerInfoDO> serverInfoDOS) {
        return serverInfoDOS.stream().map(item -> {
            ServerInfoVO serverInfoVO = new ServerInfoVO();
            BeanUtils.copyProperties(item, serverInfoVO);
            LambdaUpdateWrapper<ServerClientDO> clientQW = new LambdaUpdateWrapper<>();
            clientQW.eq(ServerClientDO::getServerId, item.getId());
            clientQW.eq(ServerClientDO::getIsLive, true);
            Long clientCnt = serverClientMapper.selectCount(clientQW);
            serverInfoVO.setLiveClientCnt(clientCnt);
            return serverInfoVO;
        }).collect(Collectors.toList());
    }
    
    private List<SimpleServerVO> transform2Simple(List<String> serverNames) {
        return serverNames.stream().map(item -> {
            SimpleServerVO simpleServerVO = new SimpleServerVO();
            simpleServerVO.setServerName(item);
            simpleServerVO.setLabel(item);
            simpleServerVO.setValue(item);
            return simpleServerVO;
        }).collect(Collectors.toList());
    }

}
