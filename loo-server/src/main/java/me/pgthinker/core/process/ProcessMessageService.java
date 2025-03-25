package me.pgthinker.core.process;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;

/**
 * @Project: me.pgthinker.core
 * @Author: De Ning
 * @Date: 2024/10/26 18:40
 * @Description:
 */
public interface ProcessMessageService {
    void process(ChannelHandlerContext target, TransferDataMessage transferDataMessage);
}
