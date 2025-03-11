package me.pgthinker.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.common.Constants;
import me.pgthinker.config.ClientConfig;
import me.pgthinker.enums.CmdTypeProto.CmdType;
import me.pgthinker.enums.ProtocolEnum;
import me.pgthinker.exception.AuthenticationException;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.core.manager.ClientManager;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.net.Connect;
import me.pgthinker.net.tcp.TcpConnect;
import me.pgthinker.net.udp.UdpConnect;

import java.util.List;
import java.util.Map;

/**
 * @Project: me.pgthinker.core.handler
 * @Author: De Ning
 * @Date: 2024/10/29 11:43
 * @Description:
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<TransferDataMessage> {

    private ChannelHandlerContext clientCtx;
    private final ClientConfig clientConfig;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    public ClientHandler(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * 与服务端建立起连接后 发送认证消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.clientCtx = ctx;
        TransferDataMessage transferDataMessage = TransferDataMessageHelper.buildAuthMessage(clientConfig.getPassword());
        this.clientCtx.writeAndFlush(transferDataMessage);
        log.info("Client connect....");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channelGroup.close();
        this.clientCtx = null;
        log.info("Client disconnect....");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TransferDataMessage transferDataMessage) throws Exception {
        CmdType cmdType = transferDataMessage.getCmdType();
        switch (cmdType){
            case AUTH_ERR -> handleAuthErr();
            case AUTH_OK -> handleAuthOk(transferDataMessage);
            case CONNECT -> handleConnect(transferDataMessage);
            case TRANSFER -> handleTransfer(transferDataMessage);
            case DISCONNECT -> handleDisconnect(transferDataMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 认证错误异常
     */
    private void handleAuthErr(){
        this.clientCtx.close();
        this.channelGroup.close();
        throw new AuthenticationException(clientConfig.getServerHost(), clientConfig.getServerPort());
    }

    /**
     * 认证通过
     * 1. 认证通过会收到licenseKey 后续的所有message body都需要携带该字段
     * 2. 认证通过后需要开启对应的代理端口
     * @param transferDataMessage
     */
    private void handleAuthOk (TransferDataMessage transferDataMessage) {
        Map<String, String> metaData = transferDataMessage.getMetaData().getMetaDataMap();
        String licenseKey = metaData.get(Constants.LICENSE_KEY);
        log.info("connected successfully. licenseKey: {}", licenseKey);
        TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
        // 请求服务端开放端口 启动用于访问的Server
        List<ProxyConfig> proxies = clientConfig.getProxies();
        for(ProxyConfig proxyConfig: proxies){
            TransferDataMessage openServerMessage = transferDataMessageHelper.buildOpenServerMessage(proxyConfig);
            this.clientCtx.writeAndFlush(openServerMessage);
        }
    }

    /**
     * 服务端发起代理连接请求
     * @param transferDataMessage
     * @throws InterruptedException
     */
    private void handleConnect(TransferDataMessage transferDataMessage) throws InterruptedException {
        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        ProxyConfig proxyConfig = ProxyConfig.fromMap(metaDataMap);
        String visitorId = metaDataMap.get(Constants.VISITOR_ID);
        String protocol = proxyConfig.getProtocol();
        Connect connect = null;
        ChannelInitializer channelInitializer = null;
        ChannelHandlerContext serverCtx = this.clientCtx;
        if(protocol.equals(ProtocolEnum.TCP.getValue())) {
            connect = new TcpConnect(new NioEventLoopGroup());
            channelInitializer = new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    LocalProxyHandler localProxyHandler = new LocalProxyHandler(serverCtx, metaDataMap);
                    socketChannel.pipeline().addLast(localProxyHandler);
                    ClientManager.setLocalProxyChannel(visitorId, localProxyHandler);
                    channelGroup.add(socketChannel); // 记录Channel
                }
            };
        }
        if(protocol.equals(ProtocolEnum.UDP.getValue())){
            connect = new UdpConnect(new NioEventLoopGroup());
            channelInitializer = new ChannelInitializer<DatagramChannel>() {

                @Override
                protected void initChannel(DatagramChannel datagramChannel) throws Exception {
                    LocalProxyHandler localProxyHandler = new LocalProxyHandler(serverCtx, metaDataMap);
                    datagramChannel.pipeline().addLast(localProxyHandler);
                    ClientManager.setLocalProxyChannel(visitorId, localProxyHandler);
                    channelGroup.add(datagramChannel); // 记录Channel
                }
            };
        }
        if(connect != null){
            try {
                connect.connect(proxyConfig.getHost(), proxyConfig.getPort(), channelInitializer);
            }catch (Exception e){
                log.info("error:{}", e.getMessage());
            }
            String licenseKey = metaDataMap.get(Constants.LICENSE_KEY);
            TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);
            TransferDataMessage connectedMessage = transferDataMessageHelper.buildConnectMessage(proxyConfig, visitorId);
            this.clientCtx.writeAndFlush(connectedMessage);
        }


    }

    /**
     * @param transferDataMessage
     * @throws InterruptedException
     */
    private void handleDisconnect(TransferDataMessage transferDataMessage) throws InterruptedException {
        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        String visitorId = metaDataMap.get(Constants.VISITOR_ID);
        LocalProxyHandler localProxyChannel = ClientManager.getLocalProxyChannel(visitorId);
        if(localProxyChannel != null) {
            localProxyChannel.getCtx().close();
            ClientManager.removeLocalProxyChannel(visitorId); // 移除
        }
    }

    private void handleTransfer (TransferDataMessage transferDataMessage) throws InterruptedException {
        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        String protocol = metaDataMap.get(Constants.PROXY_PROTOCOL);
        String visitorID = metaDataMap.get(Constants.VISITOR_ID);
        String licenseKey = metaDataMap.get(Constants.LICENSE_KEY);
        if(protocol.equals(ProtocolEnum.UDP.getValue())){

        }
        if(protocol.equals(ProtocolEnum.TCP.getValue())){
            byte[] bytes = transferDataMessage.getData().toByteArray();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
            LocalProxyHandler localProxyChannel = ClientManager.getLocalProxyChannel(visitorID);
            if(localProxyChannel != null){
                localProxyChannel.getCtx().writeAndFlush(byteBuf);
            }else{
                // TODO: 断开请求
                TransferDataMessageHelper helper = new TransferDataMessageHelper(licenseKey);
                helper.buildDisconnectMessage(ProxyConfig.fromMap(metaDataMap),visitorID);
            }
        }


    }

}
