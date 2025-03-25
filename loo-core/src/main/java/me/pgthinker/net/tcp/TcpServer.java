package me.pgthinker.net.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import me.pgthinker.net.Server;

/**
 * @Project: me.pgthinker.net
 * @Author: De Ning
 * @Date: 2024/10/26 18:30
 * @Description:
 */
public class TcpServer implements Server {
    @Getter
    private Channel channel;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    public TcpServer(EventLoopGroup bossGroup, EventLoopGroup workerGroup){
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    public synchronized void bind(int port, ChannelInitializer channelInitializer) {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            channel = b.bind(port).sync().channel();
        } catch (Exception e) {
//            e.printStackTrace();
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
            throw new RuntimeException(e);
        }
    }

    public synchronized void close() {
        if (channel != null) {
            channel.close();
        }
    }

}
