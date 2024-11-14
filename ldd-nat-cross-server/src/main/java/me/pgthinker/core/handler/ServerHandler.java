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
    private ChannelHandlerContext ctx;


    public ServerHandler(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
        this.processMessageFactory = SpringUtil.getBean(IProcessMessageFactory.class);
        this.serverManager = SpringUtil.getBean(ServerManager.class);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) this.ctx.channel().remoteAddress();
        log.info("\nClient disconnect. Client hostname:{} port:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        List<Integer> clientPort = serverManager.getClientPort(this.ctx);
        for (Integer openPort :
                clientPort) {
            log.info("关闭代理开放端口:{}", openPort);
            serverManager.stopTcpServer(openPort);
        }
        serverManager.removeClientPort(this.ctx);
        this.ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        InetSocketAddress inetSocketAddress = (InetSocketAddress) this.ctx.channel().remoteAddress();
        log.info("\nClient connect. Client hostname:{} port:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        serverManager.initClientPortContainers(this.ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TransferDataMessage transferDataMessage) throws Exception {
        ProcessMessageService processService = processMessageFactory.getProcessService(transferDataMessage.getCmdType());
        processService.process(ctx, transferDataMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("\nClient try connect fail... Error message:{} reason:{}", cause.getMessage(), cause.getStackTrace());
        this.ctx.close();
    }
}
