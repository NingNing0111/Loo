package me.pgthinker.core.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.common.Constants;
import me.pgthinker.config.ClientConfig;
import me.pgthinker.enums.CmdTypeProto.CmdType;
import me.pgthinker.exception.AuthenticationException;
import me.pgthinker.helper.TransferDataMessageHelper;
import me.pgthinker.core.manager.ClientManager;
import me.pgthinker.message.TransferDataMessageProto.TransferDataMessage;
import me.pgthinker.net.TcpConnect;

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

    private ChannelHandlerContext ctx;
    private final ClientConfig clientConfig;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    public ClientHandler(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TransferDataMessage transferDataMessage = TransferDataMessageHelper.buildAuthMessage(clientConfig.getPassword());
        ctx.writeAndFlush(transferDataMessage);
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        this.channelGroup.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TransferDataMessage transferDataMessage) throws Exception {
        CmdType cmdType = transferDataMessage.getCmdType();
        switch (cmdType){
            case AUTH_ERR -> handleAuthErr();
            case AUTH_OK -> handleAuthOk(transferDataMessage);
            case TRANSFER -> handleTransfer(transferDataMessage);
            case CONNECT -> handleConnect(transferDataMessage);
            case DISCONNECT -> handleDisconnect(transferDataMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.ctx.close();
        cause.printStackTrace();
    }

    /**
     * 认证错误异常
     */
    private void handleAuthErr(){
        this.ctx.close();
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
        TransferDataMessageHelper transferDataMessageHelper = new TransferDataMessageHelper(licenseKey);

        // 请求服务端开放端口 启动用于访问的Server
        List<ProxyConfig> proxies = clientConfig.getProxies();
        for(ProxyConfig proxyConfig: proxies){
            TransferDataMessage openServerMessage = transferDataMessageHelper.buildOpenServerMessage(proxyConfig);
            this.ctx.writeAndFlush(openServerMessage);
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
        TcpConnect tcpConnect = new TcpConnect();
        try {
            ChannelHandlerContext serverCtx = this.ctx;
            tcpConnect.connect(proxyConfig.getHost(), proxyConfig.getPort(), new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    LocalProxyHandler localProxyHandler = new LocalProxyHandler(serverCtx, metaDataMap);
                    socketChannel.pipeline().addLast(localProxyHandler);
                    ClientManager.setLocalProxyChannel(visitorId, localProxyHandler);
                    channelGroup.add(socketChannel); // 记录Channel
                }
            });
        }catch (Exception e){
            log.info("error:{}", e.getMessage());
        }
    }

    /**
     * 服务器发起断开请求
     * 1. 关闭本地所有Channel 释放资源
     * 2. 关闭客户端与服务端的Channel
     * @param transferDataMessage
     * @throws InterruptedException
     */
    private void handleDisconnect(TransferDataMessage transferDataMessage) throws InterruptedException {
        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        String message = metaDataMap.get(Constants.MESSAGE);
        if(StrUtil.isNotEmpty(message)){
            log.error("Error message from server: {}", message);
            this.ctx.close();
        }else{
            String visitorId = metaDataMap.get(Constants.VISITOR_ID);
            LocalProxyHandler localProxyChannel = ClientManager.getLocalProxyChannel(visitorId);
            if(localProxyChannel != null) {
                localProxyChannel.getCtx().close();
                ClientManager.removeLocalProxyChannel(visitorId); // 移除
            }
        }
    }

    private void handleTransfer (TransferDataMessage transferDataMessage) throws InterruptedException {
        Map<String, String> metaDataMap = transferDataMessage.getMetaData().getMetaDataMap();
        String visitorID = metaDataMap.get(Constants.VISITOR_ID);
        byte[] bytes = transferDataMessage.getData().toByteArray();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        LocalProxyHandler localProxyChannel = ClientManager.getLocalProxyChannel(visitorID);
        localProxyChannel.getCtx().writeAndFlush(byteBuf);
    }

}
