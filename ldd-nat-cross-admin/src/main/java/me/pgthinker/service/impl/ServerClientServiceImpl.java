package me.pgthinker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.mapper.ServerClientMapper;
import me.pgthinker.model.entity.ServerClientDO;
import me.pgthinker.service.ServerClientService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project: me.pgthinker.service.impl
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/5 18:34
 * @Description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServerClientServiceImpl implements ServerClientService {

    private final ServerClientMapper serverClientMapper;

    @Override
    public List<ServerClientDO> clientList(String serverId) {
        LambdaQueryWrapper<ServerClientDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ServerClientDO::getServerId, serverId);
        qw.eq(ServerClientDO::getIsLive, true);
        return serverClientMapper.selectList(qw);
    }
}
