package me.pgthinker.core.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.common.Constants;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.handler
 * @Author: De Ning
 * @Date: 2024/10/30 09:45
 * @Description: 用户访问的Channel Handler
 */
@Slf4j
public class ProxyHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final ServerManager serverManager;
    public ProxyHandler() {
        this.serverManager = SpringUtil.getBean(ServerManager.class);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        serverManager.removeVisitorChannel(channelId);
        channelHandle(ctx,1,channelId);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        serverManager.setVisitorChannel(channelId, ctx);
        channelHandle(ctx,0,channelId);

    }

    private void channelHandle(ChannelHandlerContext ctx, int type,String channelId) {
        // 根据openPort 获取对应的客户端
        Integer port = getPort(ctx);
        // 构建连接消息体
        Map<String, String> metaData = serverManager.getMetaData(port);
        String licenseKey = metaData.get(Constants.LICENSE_KEY);
        ProxyConfig proxyConfig = ProxyConfig.fromMap(metaData);
        TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
        TransferDataMessage message;
        if(type == 0){ // 连接消息
            message = transferDataMessageHelper.buildConnectMessage(proxyConfig, channelId);
        }else { // 断开消息
            message = transferDataMessageHelper.buildDisconnectMessage(proxyConfig, channelId);
        }

        ChannelHandlerContext clientCtx = serverManager.getClientChannelCtx(licenseKey);
        clientCtx.writeAndFlush(message);
    }

    private Integer getPort(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().localAddress();
        return socketAddress.getPort();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        Integer port = getPort(ctx);
        Map<String, String> originalMetaData = serverManager.getMetaData(port);
        Map<String, String> metaData = new HashMap<>(originalMetaData);
        metaData.put(Constants.VISITOR_ID, channelId);
        String licenseKey = metaData.get(Constants.LICENSE_KEY);
        TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
        TransferDataMessage transferDataMessage = transferDataMessageHelper.buildTransferMessage(metaData, byteBuf);
        ChannelHandlerContext clientCtx = serverManager.getClientChannelCtx(licenseKey);

        // 判断客户端是否连接 如果不处于连接状态 关闭本代理服务
        if(clientCtx.channel().isActive()){
            clientCtx.writeAndFlush(transferDataMessage);
        }else{
            log.info("port:{}",port);
            serverManager.stopTcpServer(port);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("err:{}", cause.getMessage());
        ctx.close();
    }
}
