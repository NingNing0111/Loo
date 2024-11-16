package me.pgthinker.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;

/**
 * @Project: me.pgthinker.handler
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/12 15:57
 * @Description:
 */
@Slf4j
public class ServerIdleStateTrigger extends SimpleChannelInboundHandler<TransferDataMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TransferDataMessage transferDataMessage) throws Exception {
        log.info("message:{}", transferDataMessage);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE){
                log.info("event:{}", event);
            }
        } else {
            super.userEventTriggered(ctx,evt);
        }
    }
}
