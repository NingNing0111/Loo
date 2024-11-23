package me.pgthinker.core.process.impl;

import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.annotation.AuthMessage;
import me.pgthinker.common.Constants;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Project: me.pgthinker.core.process.impl
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/17 16:54
 * @Description:
 */
@RequiredArgsConstructor
@Component("processConnectServerMessage")
@Slf4j
public class ProcessConnectMessageService implements ProcessMessageService {

    private final ServerManager serverManager;

    /**
     * 客户端成功建立起真实通道的连接 并发送回来消息 此时设置VisitorChannel未可读
     * @param target
     * @param transferDataMessage
     */
    @AuthMessage
    @Override
    public void process(ChannelHandlerContext target, TransferDataMessage transferDataMessage) {
        Map<String, String> metaDataMap =
                transferDataMessage.getMetaData().getMetaDataMap();
        String visitorId = metaDataMap.get(Constants.VISITOR_ID);
        ChannelHandlerContext visitorCtx = serverManager.getVisitorCtx(visitorId);
        visitorCtx.channel().config().setOption(ChannelOption.AUTO_READ, true);
    }
}
