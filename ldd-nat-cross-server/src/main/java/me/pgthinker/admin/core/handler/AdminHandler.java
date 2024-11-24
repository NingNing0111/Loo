package me.pgthinker.admin.core.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.helper.AdminDataHelper;
import me.pgthinker.admin.enums.AdminCmdTypeProto.CmdType;
import me.pgthinker.admin.message.AdminTransferDataMessageProto.TransferDataMessage;

/**
 * @Project: me.pgthinker.admin.core.handler
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 21:35
 * @Description:
 */
@Slf4j
@RequiredArgsConstructor
public class AdminHandler extends SimpleChannelInboundHandler<TransferDataMessage> {

    private final String serverName;
    private ChannelHandlerContext adminCtx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.adminCtx = ctx;
        TransferDataMessage transferDataMessage = AdminDataHelper.buildRegisterMessage(serverName);
        this.adminCtx.writeAndFlush(transferDataMessage);
        log.info("Connected to admin successful...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        log.info("Disconnected from admin successful...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransferDataMessage msg) throws Exception {
        CmdType cmdType = msg.getCmdType();
        switch (cmdType) {
            case DISCONNECT -> {
                this.adminCtx.close();
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.adminCtx.close();
        log.error("admin channel has error: {}", cause.getMessage());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
        if(idleStateEvent.state()== IdleState.WRITER_IDLE){
            TransferDataMessage transferDataMessage = AdminDataHelper.buildSystemInfoMessage();
            ctx.writeAndFlush(transferDataMessage);
        }

    }
}
