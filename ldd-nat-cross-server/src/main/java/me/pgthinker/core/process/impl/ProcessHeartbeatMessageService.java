package me.pgthinker.core.process.impl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.annotation.AuthMessage;
import me.pgthinker.annotation.MessageLog;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.message.TransferDataMessageProto;
import org.springframework.stereotype.Component;

/**
 * @Project: me.pgthinker.core.process.impl
 * @Author: De Ning
 * @Date: 2024/10/29 16:01
 * @Description:
 */
@Component("processHeartbeatMessage")
@Slf4j
@RequiredArgsConstructor
public class ProcessHeartbeatMessageService implements ProcessMessageService {

    @AuthMessage
    @MessageLog
    @Override
    public void process(ChannelHandlerContext target, TransferDataMessageProto.TransferDataMessage transferDataMessage) {

    }
}
