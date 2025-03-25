package me.pgthinker.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * @Project: me.pgthinker.net
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/10 11:32
 * @Description:
 */
public interface Server {

    void bind(int port, ChannelInitializer channelInitializer);

    void close();

    Channel getChannel();
}
