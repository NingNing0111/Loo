package me.pgthinker.core.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.common.Constants;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.util.ChannelUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.handler
 * @Author: De Ning
 * @Date: 2024/10/30 09:45
 * @Description: 用户访问的Channel Handler
 */
@Slf4j
public class TcpProxyHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ServerManager serverManager;

    public TcpProxyHandler() {
        this.serverManager = SpringUtil.getBean(ServerManager.class);
    }

    /**
     * 发送Disconnect消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 根据openPort 获取对应的客户端
        Integer port = ChannelUtil.getPort(ctx);
        ChannelHandlerContext clientChannelCtx = serverManager.getClientChannelCtx(port);
        if (clientChannelCtx == null) {
            ctx.close();
            return;
        }
        Map<String, String> meta = serverManager.getMetaData(port);
        if (meta != null) {
            String licenseKey = meta.get(Constants.LICENSE_KEY);
            ProxyConfig proxyConfig = ProxyConfig.fromMap(meta);
            String visitorId = serverManager.getTcpVisitorId(ctx);
            TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
            TransferDataMessage message;
            message = transferDataMessageHelper.buildDisconnectMessage(proxyConfig, visitorId);
            clientChannelCtx.writeAndFlush(message);
        }
        ctx.close();
        serverManager.removeVisitorCtx(ctx);
    }

    /**
     * 发送Connect消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 根据openPort 获取对应的客户端
        Integer port = ChannelUtil.getPort(ctx);
        ChannelHandlerContext clientChannelCtx = serverManager.getClientChannelCtx(port);
        // 还没有对应的客户端Channel 则关闭
        if (clientChannelCtx == null) {
            ctx.channel().close();
            return;
        }
        // 端口没有对应的映射信息
        Map<String, String> meta = serverManager.getMetaData(port);
        if (meta == null) {
            ctx.channel().close();
            return;
        }
        String visitorId = serverManager.addTcpVisitorCtx(ctx);
        // 等待实际的连接成功 再允许读
        ctx.channel().config().setOption(ChannelOption.AUTO_READ, false);
        String licenseKey = meta.get(Constants.LICENSE_KEY);
        ProxyConfig proxyConfig = ProxyConfig.fromMap(meta);
        TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
        TransferDataMessage message;
        message = transferDataMessageHelper.buildConnectMessage(proxyConfig, visitorId);
        clientChannelCtx.writeAndFlush(message);
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        log.info("请求数据:{}", byteBuf.toString(Charset.defaultCharset()));
        Integer port = ChannelUtil.getPort(ctx);
        ChannelHandlerContext clientChannelCtx = serverManager.getClientChannelCtx(port);
        if (clientChannelCtx == null) {
            ctx.channel().close();
            return;
        }
        ctx.channel().config().setAutoRead(clientChannelCtx.channel().isWritable());
        String visitorId = serverManager.getTcpVisitorId(ctx);
        Map<String, String> meta = serverManager.getMetaData(port);
        if (meta != null) {
            HashMap<String, String> data = new HashMap<>(meta);
            data.put(Constants.VISITOR_ID, visitorId);
            String licenseKey = meta.get(Constants.LICENSE_KEY);
            TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
            TransferDataMessage transferDataMessage = transferDataMessageHelper.buildTransferMessage(data, byteBuf);
            clientChannelCtx.writeAndFlush(transferDataMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("\nerr:{}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Integer port = ChannelUtil.getPort(ctx);

        ChannelHandlerContext clientChannelCtx = serverManager.getClientChannelCtx(port);
        if(clientChannelCtx == null) {
            ctx.close();
            return;
        }
        clientChannelCtx.channel().config().setOption(ChannelOption.AUTO_READ, ctx.channel().isWritable());
        super.channelWritabilityChanged(ctx);
    }
}
