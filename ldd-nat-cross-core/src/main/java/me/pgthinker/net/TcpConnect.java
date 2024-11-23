package me.pgthinker.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

/**
 * @Project: me.pgthinker.net
 * @Author: De Ning
 * @Date: 2024/10/29 13:35
 * @Description:
 */
public class TcpConnect {


    @Getter
    private Channel channel;

    private final NioEventLoopGroup workerGroup;

    public TcpConnect(NioEventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }


    public void connect(String host, int port, ChannelInitializer<SocketChannel> channelInitializer) throws InterruptedException{
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(channelInitializer);
            channel = b.connect(host, port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> workerGroup.shutdownGracefully());
        } catch (Exception e){
            workerGroup.shutdownGracefully();
            throw e;
        }
    }

}
