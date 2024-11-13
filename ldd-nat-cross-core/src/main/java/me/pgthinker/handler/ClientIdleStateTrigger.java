package me.pgthinker.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.enums.CmdTypeProto.CmdType;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;

import java.nio.charset.Charset;


/**
 * @Project: me.pgthinker.handler
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/12 12:40
 * @Description:
 */
@Slf4j
@ChannelHandler.Sharable
public class ClientIdleStateTrigger extends SimpleChannelInboundHandler<TransferDataMessage> {


    int readIdleTimes = 0;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TransferDataMessage transferDataMessage) throws Exception {
        String s = transferDataMessage.getData().toString(Charset.defaultCharset());
        CmdType cmdType = transferDataMessage.getCmdType();
        log.info(" =======> [server] message received: " + s);
        if(cmdType.getNumber() == CmdType.HEARTBEAT_VALUE) {
            TransferDataMessage heartbeatMessage = TransferDataMessageHelper.buildHeartbeatMessage("ok");
            channelHandlerContext.channel().writeAndFlush(heartbeatMessage);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        String type = null;
        switch (event.state()) {
            case READER_IDLE -> {
                readIdleTimes++;
                type = "读空闲";
                break;
            }
            case WRITER_IDLE -> {
                type = "写空闲";
                break;
            }
            case ALL_IDLE -> {
                type = "读写空闲";
                break;
            }
        }
        log.info("{} 超时事件: {}" , ctx.channel().remoteAddress(), type);
        if(readIdleTimes > 3) {
            log.info("[server] disconnect.");
            ctx.channel().writeAndFlush("[server] close...");
            ctx.channel().close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("=== {} is active ===", ctx.channel().remoteAddress());
    }

}
