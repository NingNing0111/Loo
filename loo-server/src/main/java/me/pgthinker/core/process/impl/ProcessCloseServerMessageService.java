package me.pgthinker.core.process.impl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.annotation.AuthMessage;
import me.pgthinker.annotation.MessageLog;
import me.pgthinker.common.Constants;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Project: me.pgthinker.core.process.impl
 * @Author: De Ning
 * @Date: 2024/10/29 16:04
 * @Description: 关闭代理服务
 */
@Slf4j
@RequiredArgsConstructor
@Component("processCloseServerMessage")
public class ProcessCloseServerMessageService implements ProcessMessageService {

    private final ServerManager serverManager;

    @AuthMessage
    @MessageLog
    @Override
    public void process(ChannelHandlerContext target, TransferDataMessage transferDataMessage) {
        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        String openPort = metaDataMap.get(Constants.OPEN_PORT);
        serverManager.stopServer(Integer.parseInt(openPort));
    }
}
