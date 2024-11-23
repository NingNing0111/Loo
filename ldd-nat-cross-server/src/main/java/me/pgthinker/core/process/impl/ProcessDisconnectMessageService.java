package me.pgthinker.core.process.impl;

import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.annotation.AuthMessage;
import me.pgthinker.annotation.MessageLog;
import me.pgthinker.common.Constants;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.process.impl
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/12 03:12
 * @Description: 客户端发起断开连接请求
 */
@Component("processDisconnectMessage")
@Slf4j
@RequiredArgsConstructor
public class ProcessDisconnectMessageService implements ProcessMessageService {
    private final ServerManager serverManager;

    @AuthMessage
    @Override
    public void process(ChannelHandlerContext target, TransferDataMessage transferDataMessage) {
        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        String licenseKey = metaDataMap.get(Constants.LICENSE_KEY);
        String visitorId = metaDataMap.get(Constants.VISITOR_ID);
        ChannelHandlerContext visitorCtx = serverManager.getVisitorCtx(visitorId);
        visitorCtx.close();
        serverManager.removeVisitorCtx(visitorId);
        TransferDataMessageHelper helper = new TransferDataMessageHelper(licenseKey);
        TransferDataMessage disconnectMessage = helper.buildDisconnectMessage(ProxyConfig.fromMap(metaDataMap), visitorId);
        target.writeAndFlush(disconnectMessage);
    }
}
