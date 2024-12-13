package me.pgthinker.core.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.common.AdminConstants;
import me.pgthinker.admin.enums.AdminCmdTypeProto.CmdType;
import me.pgthinker.admin.message.AdminDataProto.AdminData;
import me.pgthinker.admin.message.AdminTransferDataMessageProto.TransferDataMessage;
import me.pgthinker.core.Manager.AdminManager;
import me.pgthinker.service.ServerInfoService;
import me.pgthinker.service.ServerSystemInfoService;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.handler
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 22:58
 * @Description:
 */
@Slf4j
public class AdminHandler extends SimpleChannelInboundHandler<TransferDataMessage> {

    private final ServerInfoService serverInfoService;
    private final ServerSystemInfoService serverSystemInfoService;

    public AdminHandler() {
        this.serverInfoService = SpringUtil.getBean(ServerInfoService.class);
        this.serverSystemInfoService = SpringUtil.getBean(ServerSystemInfoService.class);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("\nServer connect. Server hostname:{} port:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("\nServer disconnect. Server hostname:{} port:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        AdminManager.unRegisterServer(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            if (IdleState.READER_IDLE.equals((event.state()))) {
                // 没有接收到读消息 端口客户端
//                ctx.close();
                log.info("读事件超时...");
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("\nServer try connect fail... Error message:{} ", cause.getMessage());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TransferDataMessage msg) throws Exception {
        CmdType cmdType = msg.getCmdType();
        switch (cmdType) {
            case REGISTER -> handleRegister(ctx, msg);
            case TRANSFER -> handleTransfer(ctx, msg);
        }
    }

    private void handleRegister(ChannelHandlerContext serverCtx, TransferDataMessage msg) {
        AdminData data = msg.getData();
        Map<String, String> metaData = data.getMetaDataMap();
        String serverName = metaData.get(AdminConstants.SERVER_NAME);
        AdminManager.registerServer(serverName,serverCtx);
        this.serverInfoService.addServerInfo(serverCtx,metaData);
    }

    private void handleTransfer(ChannelHandlerContext serverCtx, TransferDataMessage msg) {
        this.serverSystemInfoService.addSystemInfo(serverCtx.channel().id().asLongText(), msg.getData().getMetaDataMap());
    }
}
