package me.pgthinker.core.initializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.config.ServerConfig;
import me.pgthinker.core.handler.ServerHandler;
import me.pgthinker.handler.ServerIdleStateTrigger;
import me.pgthinker.message.TransferDataMessageProto;
import me.pgthinker.net.TcpServer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Project: me.pgthinker.initializer
 * @Author: De Ning
 * @Date: 2024/10/7 16:57
 * @Description:
 */
@RequiredArgsConstructor
public class ServerInitializer extends ChannelInitializer<NioSocketChannel> {

    private final TcpServer tcpServer;

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        ChannelPipeline pipeline = nioSocketChannel.pipeline();

        pipeline.addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS));

        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(TransferDataMessageProto.TransferDataMessage.getDefaultInstance()));
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
        // 服务端的处理器
        pipeline.addLast(new ServerHandler(tcpServer));

    }
}
