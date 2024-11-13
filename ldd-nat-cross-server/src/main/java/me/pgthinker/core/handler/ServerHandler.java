package me.pgthinker.core.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.core.factory.IProcessMessageFactory;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.net.TcpServer;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @Project: me.pgthinker.core.handler
 * @Author: De Ning
 * @Date: 2024/10/26 18:35
 * @Description:
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<TransferDataMessage> {

    private final IProcessMessageFactory processMessageFactory;
    private final TcpServer tcpServer;
    private ChannelHandlerContext ctx;


    public ServerHandler(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
        this.processMessageFactory = SpringUtil.getBean(IProcessMessageFactory.class);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("Client disconnect. Client hostName:{}", inetSocketAddress.getHostName());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TransferDataMessage transferDataMessage) throws Exception {
        ProcessMessageService processService = processMessageFactory.getProcessService(transferDataMessage.getCmdType());
        processService.process(ctx, transferDataMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client try connect fail... Error message:{} reason:{}", cause.getMessage(), cause.getStackTrace());
        this.ctx.close();
    }
}
