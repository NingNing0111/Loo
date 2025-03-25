package me.pgthinker.core.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.common.Constants;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.util.ChannelUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.handler
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/10 14:20
 * @Description:
 */
@Slf4j
public class UdpProxyHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final ServerManager serverManager;

    public UdpProxyHandler() {
        this.serverManager = SpringUtil.getBean(ServerManager.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {

        Integer port = ChannelUtil.getPort(ctx);
        ChannelHandlerContext clientChannelCtx = serverManager.getClientChannelCtx(port);

        if (clientChannelCtx == null) {
            return;
        }

        // 获取客户端地址信息
        InetSocketAddress sender = packet.sender();
        String visitorId = serverManager.addUdpVisitorCtx(sender);

        // 获取元数据
        Map<String, String> meta = serverManager.getMetaData(port);
        log.info("meta:{}", meta);
        if (meta == null) return;

        // 构造传输消息
        String licenseKey = meta.get(Constants.LICENSE_KEY);
        ByteBuf content = packet.content().retain();
        TransferDataMessageHelper helper = new TransferDataMessageHelper(licenseKey);

        // 在元数据中添加UDP特定信息
        HashMap<String, String> data = new HashMap<>(meta);
        data.put(Constants.VISITOR_ID, visitorId);
        data.put(Constants.UDP_REMOTE_IP, sender.getAddress().getHostAddress());
        data.put(Constants.UDP_REMOTE_PORT, String.valueOf(sender.getPort()));

        TransferDataMessage message = helper.buildTransferMessage(data, content);
        clientChannelCtx.writeAndFlush(message);

        content.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught: {}", cause.getMessage());
        cause.printStackTrace();
        ctx.close(); // 关闭连接
    }
}
