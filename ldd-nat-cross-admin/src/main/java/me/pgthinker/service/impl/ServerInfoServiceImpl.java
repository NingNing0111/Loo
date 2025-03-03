package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.pgthinker.mapper.ServerInfoMapper;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.vo.ServerInfoVO;
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

    @Override
    public List<ServerInfoVO> list() {
        List<ServerInfoVO> res = new ArrayList<>();
        LambdaQueryWrapper<ServerInfoDO> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(ServerInfoDO::getRegisterTime);
        List<ServerInfoDO> serverInfoDOS = serverInfoMapper.selectList(qw);
        Map<String, List<ServerInfoDO>> groupList = serverInfoDOS.stream().collect(Collectors.groupingBy(ServerInfoDO::getServerName));
        for (String key : groupList.keySet()) {
            List<ServerInfoDO> groupServers = groupList.get(key);
            ServerInfoDO serverInfoDO = groupServers.get(0);
            ServerInfoVO serverInfoVO = new ServerInfoVO();
            BeanUtils.copyProperties(serverInfoDO, serverInfoVO);
            res.add(serverInfoVO);
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

    private List<ServerInfoVO> transform(List<ServerInfoDO> serverInfoDOS) {
        return serverInfoDOS.stream().map(item -> {
            ServerInfoVO serverInfoVO = new ServerInfoVO();
            BeanUtils.copyProperties(item, serverInfoVO);
            return serverInfoVO;
        }).collect(Collectors.toList());
    }

}
