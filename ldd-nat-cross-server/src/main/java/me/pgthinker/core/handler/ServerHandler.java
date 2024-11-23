package me.pgthinker.core.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.core.factory.IProcessMessageFactory;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.net.TcpServer;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Project: me.pgthinker.core.handler
 * @Author: De Ning
 * @Date: 2024/10/26 18:35
 * @Description:
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<TransferDataMessage> {

    private final IProcessMessageFactory processMessageFactory;
    private final ServerManager serverManager;
    private final TcpServer tcpServer;

    public ServerHandler(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
        this.processMessageFactory = SpringUtil.getBean(IProcessMessageFactory.class);
        this.serverManager = SpringUtil.getBean(ServerManager.class);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("\nClient disconnect. Client hostname:{} port:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        // 清理meta
        String licenseKey = serverManager.getClientLicenseKey(ctx);
        serverManager.removeMetaData(licenseKey);
        // 清理客户端Channel
        serverManager.removeClientChannel(ctx);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("\nClient connect. Client hostname:{} port:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TransferDataMessage transferDataMessage) throws Exception {
        log.debug("cmd:{}", transferDataMessage.getCmdType());
        ProcessMessageService processService = processMessageFactory.getProcessService(transferDataMessage.getCmdType());
        processService.process(channelHandlerContext, transferDataMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("\nClient try connect fail... Error message:{} ", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
