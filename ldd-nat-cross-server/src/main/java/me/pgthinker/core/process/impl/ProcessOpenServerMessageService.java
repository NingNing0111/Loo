package me.pgthinker.core.process.impl;

import cn.hutool.core.net.NetUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.annotation.AuthMessage;
import me.pgthinker.annotation.MessageLog;
import me.pgthinker.common.Constants;
import me.pgthinker.core.handler.TcpProxyHandler;
import me.pgthinker.core.handler.UdpProxyHandler;
import me.pgthinker.core.process.ProcessMessageService;
import me.pgthinker.core.manager.ServerManager;
import me.pgthinker.enums.ProtocolEnum;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.net.Server;
import me.pgthinker.net.tcp.TcpServer;
import me.pgthinker.net.udp.UdpServer;
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
        serverManager.addProxyPort(licenseKey, proxyConfig.getOpenPort());
        serverManager.addMetaData(proxyConfig.getOpenPort(),metaDataMap);
        // 构建消息

        Integer port = proxyConfig.getOpenPort();
        boolean isUsablePort = NetUtil.isUsableLocalPort(port);
        String protocol = proxyConfig.getProtocol();
        // 端口不可用
        if (!isUsablePort) {
            TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
            TransferDataMessage disconnectMsg = transferDataMessageHelper.buildDisconnectMessage(proxyConfig);
            target.writeAndFlush(disconnectMsg);
            return;
        }
        // 根据协议tcp/udp启动服务
        Server server = null;
        ChannelInitializer channelInitializer = null;
        if(protocol.equals(ProtocolEnum.TCP.getValue())) {
            server = new TcpServer(boss, worker);
            channelInitializer = new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    TcpProxyHandler tcpProxyHandler = new TcpProxyHandler();
                    ch.pipeline().addLast(tcpProxyHandler);
                }
            };
        }
        if(protocol.equals(ProtocolEnum.UDP.getValue())) {
            server = new UdpServer(worker);
            channelInitializer = new ChannelInitializer<NioDatagramChannel>() {
                @Override
                protected void initChannel(NioDatagramChannel ch) throws Exception {
                    UdpProxyHandler udpProxyHandler = new UdpProxyHandler();
                    ch.pipeline().addLast(udpProxyHandler);
                }
            };
        }
        if(server != null) {
            try {
                server.bind(port, channelInitializer);
                serverManager.addServer(port,server);
                log.info("启动代理端口:{}", port);
            } catch (Exception e) {
                throw e;
            }
        }


    }
}
