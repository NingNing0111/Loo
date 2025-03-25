package me.pgthinker.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * @Project: me.pgthinker.net
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/10 11:35
 * @Description:
 */
public interface Connect {
    void connect(String host, int port, ChannelInitializer channelInitializer);
    Channel getChannel();
}
