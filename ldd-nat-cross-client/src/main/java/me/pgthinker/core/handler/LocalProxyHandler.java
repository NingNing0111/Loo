package me.pgthinker.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.common.Constants;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.handler
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/12 02:41
 * @Description: 目标代理程序（如MySQL、Redis、WebApp等）的响应Handler,接收到来自目标代理程序的数据后，将数据封装为TransferDataMessage发送到服务端
 */
@Slf4j
@RequiredArgsConstructor
public class LocalProxyHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final ChannelHandlerContext serverChannelCtx;// 这个ctx是客户端与服务端之间的ctx
    private final Map<String, String> metaData;

    @Getter
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    /**
     * 有读数据 封装TransferDataMessage 发送给server
     * @param channelHandlerContext
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        log.info("data:{}", byteBuf);
        String licenseKey = metaData.get(Constants.LICENSE_KEY);
        TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
        TransferDataMessage transferDataMessage = transferDataMessageHelper.buildTransferMessage(metaData, byteBuf);
        serverChannelCtx.writeAndFlush(transferDataMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.info("Local proxy exception. Exception message:{} Reason:{}", cause.getMessage(), cause.getStackTrace());
    }


}
