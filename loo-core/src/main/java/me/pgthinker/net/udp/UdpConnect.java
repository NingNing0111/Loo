package me.pgthinker.net.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import me.pgthinker.net.Connect;

import java.net.InetSocketAddress;

/**
 * @Project: me.pgthinker.net
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/10 11:17
 * @Description:
 */
public class UdpConnect implements Connect {
    @Getter
    private Channel channel;

    private final NioEventLoopGroup workerGroup;

    public UdpConnect(NioEventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    public void connect(String host, int port, ChannelInitializer channelInitializer) {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioDatagramChannel.class);
            bootstrap.option(ChannelOption.SO_BROADCAST, true);  // 可选：启用广播
            bootstrap.handler(channelInitializer);

            // 启动连接
            channel = bootstrap.connect(new InetSocketAddress(host, port)).sync().channel();

            // 监听关闭事件，关闭时优雅地释放资源
            channel.closeFuture().addListener((ChannelFutureListener) future -> workerGroup.shutdownGracefully());
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            throw new RuntimeException(e);
        }
    }

}
