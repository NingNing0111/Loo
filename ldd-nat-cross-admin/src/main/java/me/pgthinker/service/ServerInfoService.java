package me.pgthinker.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.channel.ChannelHandlerContext;
import me.pgthinker.model.entity.ServerInfoDO;
import me.pgthinker.model.vo.ServerInfoVO;
import org.springdoc.core.converters.models.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.service
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:47
 * @Description:
 */
public interface ServerInfoService {

    void addServerInfo(ChannelHandlerContext ctx, Map<String, String> serverInfo);

    List<ServerInfoVO> list();

    Page<ServerInfoVO> historyList(ServerInfoVO serverInfoVO, Integer page, Integer pageSize);

}
