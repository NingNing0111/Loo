package me.pgthinker.core.manager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.common.Constants;
import me.pgthinker.net.Server;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Project: me.pgthinker.manager
 * @Author: De Ning
 * @Date: 2024/10/26 18:53
 * @Description: 服务端一些Ctx和配置的管理
 * 1. 当客户端与服务端建立起连接后，构建LicenseKey->ChannelHandlerContext；
 * 2. 当客户端发起开放端口时，构建openPort->licenseKey映射；
 * 3. 当访问通道建立时 构建： ChannelHandlerContext -> visitorId 映射， 其中visitorId 为UUID
 * 4. 服务端接收到来自LocalProxy的代理消息时, 通过openPort找到对应的LicenseKey,再通过licenseKey找到对应的ClientContext，携带visitorId向客户端发送数据
 * 5. 服务端接收到来自客户端代理的数据时，根据visitorId找到对应的Ctx写入
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ServerManager {

    // licenseKey -> clientCtx
    private static final Map<String, ChannelHandlerContext> CLIENT_CHANNEL = new ConcurrentHashMap<>();
    // openPort -> licenseKey
    private static final Map<Integer, String> LICENSE_KEY_DATA = new ConcurrentHashMap<>();
    // openPort -> server
    private static final Map<Integer, Server> PROXY_SERVER_DATA = new ConcurrentHashMap<>();
    // openPort -> meta
    private static final Map<Integer, Map<String,String>> PROXY_CONFIG_MAP = new ConcurrentHashMap<>();
    // visitorCtx -> visitorId
    private static final Map<ChannelHandlerContext, String> TCP_VISITOR_CHANNEL_CTX = new ConcurrentHashMap<>();
    private static final Map<InetSocketAddress, String> UDP_VISITOR_CHANNEL_CTX = new ConcurrentHashMap<>();
    /**
     * 读写锁
     */
    private static final ReadWriteLock userChannelMapLock = new ReentrantReadWriteLock();

    /**
     * 客户端与服务端进行认证授权后 得到clientChannel->licenseKey的映射
     * @param clientChannel
     * @return 授权密钥
     *
     *        |--id-1--- client1
     * server |--id-2--- client2
     *        |--id-3--- client3
     *
     */
    public String addClientChannel(ChannelHandlerContext clientChannel) {
        String licenseKey = MD5.create().digestHex16(UUID.randomUUID().toString());
        CLIENT_CHANNEL.put(licenseKey, clientChannel);
        return licenseKey;
    }

    public String getClientLicenseKey(ChannelHandlerContext ctx) {
        for(String licenseKey: CLIENT_CHANNEL.keySet()){
            ChannelHandlerContext c = CLIENT_CHANNEL.get(licenseKey);
            if(c.channel().id().equals(ctx.channel().id())){
                return licenseKey;
            }
        }
        return null;
    }

    public void removeClientChannel(ChannelHandlerContext ctx) {
        for(String licenseKey: CLIENT_CHANNEL.keySet()){
            ChannelHandlerContext c = CLIENT_CHANNEL.get(licenseKey);
            if(c.channel().id().equals(ctx.channel().id())){
                CLIENT_CHANNEL.remove(licenseKey);
                break;
            }
        }
    }

    /**
     * 一个ChannelHandlerContext
     * @param openPort
     * @return
     */
    public ChannelHandlerContext getClientChannelCtx(Integer openPort) {
        String licenseKey = LICENSE_KEY_DATA.get(openPort);
        if(!licenseKey.isEmpty()){
            return this.getClientChannelCtx(licenseKey);
        }
        return null;
    }

    /**
     *
     * @param licenseKey
     * @return
     */
    public ChannelHandlerContext getClientChannelCtx(String licenseKey) {
        return CLIENT_CHANNEL.get(licenseKey);
    }

    public void addMetaData(Integer openPort, Map<String,String> metaData) {
        PROXY_CONFIG_MAP.put(openPort, metaData);
    }

    public Map<String, String> getMetaData(Integer openPort){
        return PROXY_CONFIG_MAP.getOrDefault(openPort, null);
    }

    public void removeMetaData(String licenseKey) {
        for(Integer port: PROXY_CONFIG_MAP.keySet()){
            Map<String, String> maps = PROXY_CONFIG_MAP.get(port);
            String storeKey = maps.get(Constants.LICENSE_KEY);
            if(StrUtil.equals(licenseKey, storeKey)){
               PROXY_CONFIG_MAP.remove(port);
            }
        }
    }

    // 当有visitor连接时 上锁
    public String addTcpVisitorCtx(ChannelHandlerContext visitorCtx){
        String visitorId = visitorCtx.channel().id().asLongText();
        userChannelMapLock.writeLock().lock();
        try {
            TCP_VISITOR_CHANNEL_CTX.put(visitorCtx, visitorId);
        }finally {
            userChannelMapLock.writeLock().unlock();
        }

        return visitorId;
    }

    public String addUdpVisitorCtx(InetSocketAddress udpAddress){
        String visitorId = MD5.create().digestHex(udpAddress.getHostString());
        userChannelMapLock.writeLock().lock();
        try {
            UDP_VISITOR_CHANNEL_CTX.put(udpAddress, visitorId);
        }finally {
            userChannelMapLock.writeLock().unlock();
        }
        return visitorId;
    }

    public void removeVisitorCtx(ChannelHandlerContext visitorCtx) {
        userChannelMapLock.writeLock().lock();
        try {
            this.removeVisitorCtx(visitorCtx.channel().id().asLongText());
        } finally {
            userChannelMapLock.writeLock().unlock();
        }
    }

    public void removeVisitorCtx(String visitorId){
        userChannelMapLock.readLock().lock();
        try {
            for(ChannelHandlerContext ctx: TCP_VISITOR_CHANNEL_CTX.keySet()) {
                String storeVisitorId = TCP_VISITOR_CHANNEL_CTX.get(ctx);
                if(visitorId.equals(storeVisitorId)){
                    TCP_VISITOR_CHANNEL_CTX.remove(ctx);
                }
            }

            for(InetSocketAddress inetSocketAddress: UDP_VISITOR_CHANNEL_CTX.keySet()){
                String storeVisitorId = UDP_VISITOR_CHANNEL_CTX.get(inetSocketAddress);
                if(storeVisitorId.equals(visitorId)){
                    UDP_VISITOR_CHANNEL_CTX.remove(inetSocketAddress);
                }
            }
        } finally {
            userChannelMapLock.readLock().unlock();
        }

    }

    public ChannelHandlerContext getTcpVisitorCtx(String visitorId){
        for(ChannelHandlerContext ctx: TCP_VISITOR_CHANNEL_CTX.keySet()){
            String vId = TCP_VISITOR_CHANNEL_CTX.get(ctx);
            if(StrUtil.equals(vId, visitorId)){
                return ctx;
            }
        }
        return null;
    }

    public InetSocketAddress getUdpVisitorAddress(String visitorId){
        for (InetSocketAddress address : UDP_VISITOR_CHANNEL_CTX.keySet()) {
            String vId = UDP_VISITOR_CHANNEL_CTX.get(address);
            if(StrUtil.equals(vId, visitorId)){
                return address;
            }
        }
        return null;
    }

    public String getTcpVisitorId(ChannelHandlerContext ctx) {
        return TCP_VISITOR_CHANNEL_CTX.get(ctx);
    }

    public String getUdpVisitorId(InetSocketAddress udpAddress) {
        return UDP_VISITOR_CHANNEL_CTX.get(udpAddress);
    }

    public void addProxyPort(String licenseKey, Integer port) {
        LICENSE_KEY_DATA.put(port, licenseKey);
    }

    public void addServer(Integer port, Server server) {
        PROXY_SERVER_DATA.put(port, server);
    }

    public void stopServer(Integer port){
        Server server = PROXY_SERVER_DATA.get(port);
        if(server != null){
            server.close();
        }
        PROXY_SERVER_DATA.remove(port);
    }


    public List<String> getLicenseKeyList() {
        return new ArrayList<>(CLIENT_CHANNEL.keySet());
    }


}
