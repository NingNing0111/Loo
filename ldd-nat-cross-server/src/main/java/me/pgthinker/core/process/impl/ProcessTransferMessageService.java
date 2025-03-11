package me.pgthinker.core.process.impl;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.annotation.AuthMessage;
import me.pgthinker.annotation.MessageLog;
import me.pgthinker.common.Constants;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.process.impl
 * @Author: De Ning
 * @Date: 2024/10/29 16:02
 * @Description:
 */
@Component("processTransferMessage")
@Slf4j
@RequiredArgsConstructor
public class ProcessTransferMessageService implements ProcessMessageService {

    private final ServerManager serverManager;

    @AuthMessage
    @MessageLog
    @Override
    public void process(ChannelHandlerContext target, TransferDataMessage transferDataMessage) {
        Map<String, String> originalMetaData = transferDataMessage.getMetaData().getMetaDataMap();
        Map<String, String> metaDataMap = new HashMap<>(originalMetaData);
        String visitorId = metaDataMap.get(Constants.VISITOR_ID);
        ChannelHandlerContext visitorChannel = serverManager.getTcpVisitorCtx(visitorId);
        if(visitorChannel != null){
            ByteString data = transferDataMessage.getData();
            byte[] bytes = data.toByteArray();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
            visitorChannel.writeAndFlush(byteBuf);
        }
    }
}
