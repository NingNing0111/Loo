package me.pgthinker.core.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.config.ClientConfig;
import me.pgthinker.core.handler.ClientHandler;
import me.pgthinker.message.TransferDataMessageProto;

import java.util.concurrent.TimeUnit;

/**
 * @Project: me.pgthinker.core.initializer
 * @Author: De Ning
 * @Date: 2024/10/29 11:42
 * @Description:
 */
@Slf4j
@RequiredArgsConstructor
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private final ClientConfig clientConfig;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ClientHandler clientHandler = new ClientHandler(clientConfig);

        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS));
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(TransferDataMessageProto.TransferDataMessage.getDefaultInstance()));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(clientHandler);
//        pipeline.addLast(new ClientIdleStateTrigger());
    }


}
