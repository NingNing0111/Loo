package me.pgthinker.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @Project: me.pgthinker.util
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/10 14:22
 * @Description:
 */
public class ChannelUtil {
    public static Integer getPort(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        return socketAddress.getPort();
    }
}
