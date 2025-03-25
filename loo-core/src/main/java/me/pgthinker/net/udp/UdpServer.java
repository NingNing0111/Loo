package me.pgthinker.net.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import me.pgthinker.net.Server;

/**
 * @Project: me.pgthinker.net
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/10 11:17
 * @Description:
 */
public class UdpServer implements Server {
    @Getter
    private Channel channel;

    private final EventLoopGroup group;

    public UdpServer(EventLoopGroup group) {
        this.group = group;
    }

    @Override

    public synchronized void bind(int port, ChannelInitializer channelInitializer) {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(channelInitializer)
                    .option(ChannelOption.SO_BROADCAST, true);
            channel = b.bind(port).sync().channel();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void close() {
        if(channel != null) {
            channel.close();
        }
    }
}
