package me.pgthinker.core.manager;

import cn.hutool.crypto.digest.MD5;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.ProxyConfig;
import me.pgthinker.common.Constants;
import me.pgthinker.net.TcpServer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Project: me.pgthinker.manager
 * @Author: De Ning
 * @Date: 2024/10/26 18:53
 * @Description:
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ServerManager {

    // auth: licenseKey -> clientChannel
    private static final Map<String, ChannelHandlerContext> CLIENT_CHANNEL = new ConcurrentHashMap<>();
    // openPort -> visitor_id
    private static final Map<Integer, TcpServer> PROXY_SERVER_DATA = new ConcurrentHashMap<>();
    // visitor_id -> proxyChannel
    private static final Map<String, ChannelHandlerContext> VISITOR_CHANNEL = new ConcurrentHashMap<>();
    //
    private static final Map<Integer, Map<String, String>> CLIENT_META_DATA = new ConcurrentHashMap<>();
    private static final Map<ChannelHandlerContext, List<Integer>> CLIENT_PORTS = new ConcurrentHashMap<>();

    private static final Map<String, List<Integer>> CLIENT_OPEN_PORT = new ConcurrentHashMap<>();

    /**
     * 客户端与服务端进行认证授权后 得到clientChannel->licenseKey的映射
     * @param targetChannel
     * @return 授权密钥
     *
     *        |--id-1--- client1
     * server |--id-2--- client2
     *        |--id-3--- client3
     *
     */
    public String newClientChannel(ChannelHandlerContext targetChannel) {
        String licenseKey = MD5.create().digestHex16(UUID.randomUUID().toString());
        CLIENT_CHANNEL.put(licenseKey, targetChannel);
        return licenseKey;
    }

    /**
     *
     * @param licenseKey
     * @return
     */
    public ChannelHandlerContext getClientChannelCtx(String licenseKey) {
        return CLIENT_CHANNEL.get(licenseKey);
    }

    /**
     * 客户端发起代理信息 服务端开放端口 此时构建信息 metaData存放licenseKey、openPort、proxyPort、proxyHost等信息
     * @return 返回代理的client_id
     *
     *        |--id-1---- mysql
     * client |--id-2---- redis
     *        |--id-3---- minio
     *
     */
    public String newProxyClientChannel(ProxyConfig proxyConfig, String licenseKey) {
        String clientId = MD5.create().digestHex16(proxyConfig.toString() + licenseKey);
        Map<String, String> map = proxyConfig.toMap();
        map.put(Constants.LICENSE_KEY, licenseKey);
        CLIENT_META_DATA.put(proxyConfig.getOpenPort(), map);

        List<Integer> ports = CLIENT_OPEN_PORT.getOrDefault(licenseKey, new ArrayList<>());
        ports.add(proxyConfig.getPort());
        CLIENT_OPEN_PORT.put(licenseKey, ports);

        return clientId;
    }

    public void setVisitorChannel(String channelId, ChannelHandlerContext proxyCtx) {
        VISITOR_CHANNEL.put(channelId, proxyCtx);
    }

    public void removeVisitorChannel(String channelId){
        VISITOR_CHANNEL.remove(channelId);
    }

    public Map<String, String> getMetaData(Integer openPort) {
        return CLIENT_META_DATA.get(openPort);
    }

    public ChannelHandlerContext getVisitorChannel(String visitorId) {
        return VISITOR_CHANNEL.get(visitorId);
    }

    public List<String> getLicenseKeyList() {
        return new ArrayList<>(CLIENT_CHANNEL.keySet());
    }


    public void addTcpServer(Integer port, TcpServer tcpServer) {
        PROXY_SERVER_DATA.put(port, tcpServer);
    }

    public void stopTcpServer(Integer port){
        TcpServer tcpServer = PROXY_SERVER_DATA.get(port);
        if(tcpServer != null){
            tcpServer.close();
        }
        PROXY_SERVER_DATA.remove(port);
    }

    public void removePort(String licenseKey){
        CLIENT_OPEN_PORT.remove(licenseKey);
    }

    public List<Integer> getProxyPort(String licenseKey) {
        return CLIENT_OPEN_PORT.getOrDefault(licenseKey, new ArrayList<>());
    }

    public void printInfo(){
        log.info("client size:{} proxy size:{} visitor size:{}", CLIENT_CHANNEL.size(), PROXY_SERVER_DATA.size(), VISITOR_CHANNEL.size());
    }

    public void initClientPortContainers(ChannelHandlerContext clientCtx){
        CLIENT_PORTS.put(clientCtx,new ArrayList<>());
    }

    public void addClientPort(ChannelHandlerContext clientCtx, Integer port) {
        List<Integer> clientPort = this.getClientPort(clientCtx);
        clientPort.add(port);
        CLIENT_PORTS.put(clientCtx, clientPort);

    }

    public List<Integer> getClientPort(ChannelHandlerContext clientCtx){
        return CLIENT_PORTS.getOrDefault(clientCtx, new ArrayList<>());
    }

    public void removeClientPort(ChannelHandlerContext clientCtx){
        CLIENT_PORTS.remove(clientCtx);
    }
}
