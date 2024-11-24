package me.pgthinker.admin.core.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import me.pgthinker.admin.core.handler.AdminHandler;
import me.pgthinker.admin.message.AdminTransferDataMessageProto.TransferDataMessage;

import java.util.concurrent.TimeUnit;

/**
 * @Project: me.pgthinker.admin.core.initializer
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 21:32
 * @Description:
 */
@RequiredArgsConstructor
public class AdminInitializer extends ChannelInitializer<SocketChannel> {

    private final String serverName;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(TransferDataMessage.getDefaultInstance()));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());

        pipeline.addLast(new AdminHandler(serverName));
    }
}
