package me.pgthinker.core.handler;

import cn.hutool.core.util.ObjectUtil;
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

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /**
     * 发送Disconnect消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 根据openPort 获取对应的客户端
        Integer port = getPort(ctx);
        List<ChannelHandlerContext> clientChannelCtx = serverManager.getClientChannelCtx(port);
        clientChannelCtx.stream().filter(ObjectUtil::isNotEmpty).forEach(clientCtx -> {
            List<Map<String,String>> metas = serverManager.getMetaData(port);
            metas.forEach(meta->{
                String licenseKey = meta.get(Constants.LICENSE_KEY);
                ProxyConfig proxyConfig = ProxyConfig.fromMap(meta);
                String visitorId = serverManager.getVisitorId(ctx);
                TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
                TransferDataMessage message;
                message = transferDataMessageHelper.buildDisconnectMessage(proxyConfig, visitorId);
                clientCtx.writeAndFlush(message);
            });
        });
        ctx.close();
        serverManager.removeVisitorCtx(ctx);
    }

    /**
     * 发送Connect消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 根据openPort 获取对应的客户端
        Integer port = getPort(ctx);
        List<ChannelHandlerContext> clientChannelCtx = serverManager.getClientChannelCtx(port);

        String visitorId = serverManager.addVisitorCtx(ctx);
        // 构建连接消息体
        clientChannelCtx.stream().filter(ObjectUtil::isNotEmpty).forEach(clientCtx->{
            List<Map<String,String>> metas = serverManager.getMetaData(port);
            metas.forEach(metaData->{
                ctx.channel().config().setOption(ChannelOption.AUTO_READ, false);
                String licenseKey = metaData.get(Constants.LICENSE_KEY);
                ProxyConfig proxyConfig = ProxyConfig.fromMap(metaData);
                TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
                TransferDataMessage message;
                message = transferDataMessageHelper.buildConnectMessage(proxyConfig, visitorId);
                clientCtx.writeAndFlush(message);
            });

        });

    }

    private Integer getPort(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().localAddress();
        return socketAddress.getPort();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        Integer port = getPort(ctx);
        // 端口复用 一个端口对应多个客户端
        List<ChannelHandlerContext> clientChannelCtx = serverManager.getClientChannelCtx(port);
        String visitorId = serverManager.getVisitorId(ctx);
        // 将数据发布到每个客户端
        clientChannelCtx.stream().filter(ObjectUtil::isNotEmpty).forEach(clientCtx->{
            if(clientCtx.channel().config().isAutoRead()){
                List<Map<String, String>> metas = serverManager.getMetaData(port);
                metas.forEach(metaData->{
                    HashMap<String, String> data = new HashMap<>(metaData);
                    data.put(Constants.VISITOR_ID, visitorId);
                    String licenseKey = metaData.get(Constants.LICENSE_KEY);
                    TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
                    TransferDataMessage transferDataMessage = transferDataMessageHelper.buildTransferMessage(data, byteBuf);
                    clientCtx.writeAndFlush(transferDataMessage);
                });
            }else{
                serverManager.removeClientChannel(clientCtx);
                clientCtx.close();
            }

        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("\nerr:{}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
