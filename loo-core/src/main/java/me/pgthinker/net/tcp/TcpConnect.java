package me.pgthinker.net.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import me.pgthinker.net.Connect;

/**
 * @Project: me.pgthinker.net
 * @Author: De Ning
 * @Date: 2024/10/29 13:35
 * @Description:
 */
public class TcpConnect implements Connect {


    @Getter
    private Channel channel;

    private final NioEventLoopGroup workerGroup;

    public TcpConnect(NioEventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    @Override
    public void connect(String host, int port, ChannelInitializer channelInitializer){
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
            throw new RuntimeException(e);
        }
    }

}
