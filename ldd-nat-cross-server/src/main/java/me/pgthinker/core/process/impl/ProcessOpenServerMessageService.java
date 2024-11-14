package me.pgthinker.core.process.impl;

import cn.hutool.core.net.NetUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.annotation.AuthMessage;
import me.pgthinker.annotation.MessageLog;
import me.pgthinker.common.Constants;
import me.pgthinker.core.handler.ProxyHandler;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.net.TcpServer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Project: me.pgthinker.core.process.impl
 * @Author: De Ning
 * @Date: 2024/10/29 16:03
 * @Description:
 */
@Component("processOpenServerMessage")
@Slf4j
@RequiredArgsConstructor
public class ProcessOpenServerMessageService implements ProcessMessageService {

    @Resource(name = "boss")
    private NioEventLoopGroup boss;
    @Resource(name = "worker")
    private NioEventLoopGroup worker;
    private final ServerManager serverManager;


    @AuthMessage
    @MessageLog
    @Override
    public void process(ChannelHandlerContext target, TransferDataMessage transferDataMessage) {
        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        String licenseKey = metaDataMap.get(Constants.LICENSE_KEY);

        ProxyConfig proxyConfig = ProxyConfig.fromMap(metaDataMap);
        String clientId = serverManager.newProxyClientChannel(proxyConfig, licenseKey);
        TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
        TransferDataMessage connectMessage = transferDataMessageHelper.buildConnectMessage(proxyConfig, clientId);

        Integer port = proxyConfig.getOpenPort();
        boolean isUsablePort = NetUtil.isUsableLocalPort(port);

        // 如果端口可用
        if(isUsablePort) {
            TcpServer tcpServer = new TcpServer(boss, worker);
            try {
                tcpServer.bind(port, new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ProxyHandler proxyHandler = new ProxyHandler();
                        nioSocketChannel.pipeline().addLast(proxyHandler);
                    }

                });
                serverManager.addClientPort(target, port);
                serverManager.addTcpServer(port,tcpServer);
                target.writeAndFlush(connectMessage);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else {
            String message = String.format("port:[%s] already used.", port);
            TransferDataMessage disconnectMessage = transferDataMessageHelper.buildDisconnectMessage(message);
            target.writeAndFlush(disconnectMessage);
            target.close();
        }
    }
}
