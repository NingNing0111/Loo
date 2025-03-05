package me.pgthinker.core.manager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.pgthinker.admin.vo.VisitorConfigVO;
import me.pgthinker.common.Constants;
import me.pgthinker.net.TcpServer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

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
    private static final Map<Integer, List<String>> LICENSE_KEY_DATA = new ConcurrentHashMap<>();
    // openPort -> server
    private static final Map<Integer, TcpServer> PROXY_SERVER_DATA = new ConcurrentHashMap<>();
    // openPort -> meta
    private static final Map<Integer, List<Map<String,String>>> PROXY_CONFIG_MAP = new ConcurrentHashMap<>();
    // visitorCtx -> visitorId
    private static final Map<ChannelHandlerContext, String> VISITOR_CHANNEL_CTX = new ConcurrentHashMap<>();
    /**
     * cmdChannelAttachInfo.getUserChannelMap() 读写锁
     */
    private static final ReadWriteLock userChannelMapLock = new ReentrantReadWriteLock();

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
    public String addClientChannel(ChannelHandlerContext targetChannel) {
        String licenseKey = MD5.create().digestHex16(UUID.randomUUID().toString());
        CLIENT_CHANNEL.put(licenseKey, targetChannel);
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
     * 多个ChannelHandlerContext
     * @param openPort
     * @return
     */
    public List<ChannelHandlerContext> getClientChannelCtx(Integer openPort) {
        List<String> licenseKeys = LICENSE_KEY_DATA.get(openPort);
        if(!licenseKeys.isEmpty()){
            return licenseKeys.stream().map(this::getClientChannelCtx).collect(Collectors.toList());
        }
        return Collections.emptyList();
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
        // 先从 Map 中获取现有的 metas 列表
        List<Map<String, String>> metas = PROXY_CONFIG_MAP.get(openPort);
        if (metas == null) {
            metas = new ArrayList<>();
        } else {
            metas = new ArrayList<>(metas);
        }
        metas.add(metaData);
        PROXY_CONFIG_MAP.put(openPort, metas);
    }

    public List<Map<String, String>> getMetaData(Integer openPort){
        return PROXY_CONFIG_MAP.getOrDefault(openPort, null);
    }

    public void removeMetaData(String licenseKey) {
        for(Integer port: PROXY_CONFIG_MAP.keySet()){
            List<Map<String, String>> maps = PROXY_CONFIG_MAP.get(port);
            List<Map<String, String>> res = maps.stream().filter(item -> !StrUtil.equals(licenseKey, item.get(Constants.LICENSE_KEY))).toList();
            PROXY_CONFIG_MAP.put(port, res);
        }
    }

    // 当有visitor连接时 上锁
    public String addVisitorCtx(ChannelHandlerContext ctx){
        userChannelMapLock.writeLock().lock();
        String visitorId = UUID.randomUUID().toString();
        VISITOR_CHANNEL_CTX.put(ctx, visitorId);
        userChannelMapLock.writeLock().unlock();
        return visitorId;
    }

    public void removeVisitorCtx(ChannelHandlerContext ctx) {
        userChannelMapLock.writeLock().lock();
        VISITOR_CHANNEL_CTX.remove(ctx);
        userChannelMapLock.writeLock().unlock();
    }

    public void removeVisitorCtx(String visitorId){
        userChannelMapLock.readLock().lock();
        for(ChannelHandlerContext ctx: VISITOR_CHANNEL_CTX.keySet()) {
            String storeVisitorId = VISITOR_CHANNEL_CTX.get(ctx);
            if(visitorId.equals(storeVisitorId)){
                VISITOR_CHANNEL_CTX.remove(ctx);
            }
        }
        userChannelMapLock.readLock().unlock();
    }

    public ChannelHandlerContext getVisitorCtx(String visitorId){
        for(ChannelHandlerContext ctx: VISITOR_CHANNEL_CTX.keySet()){
            String vId = VISITOR_CHANNEL_CTX.get(ctx);
            if(StrUtil.equals(vId, visitorId)){
                return ctx;
            }
        }
        return null;
    }

    public String getVisitorId(ChannelHandlerContext ctx) {
        return VISITOR_CHANNEL_CTX.get(ctx);
    }

    public void addProxyPort(String licenseKey, Integer port) {
        List<String> licenseKeys;
        if(!LICENSE_KEY_DATA.containsKey(licenseKey)) {
            licenseKeys = new ArrayList<>();
        }else{
            licenseKeys = LICENSE_KEY_DATA.get(port);
        }
        licenseKeys.add(licenseKey);
        LICENSE_KEY_DATA.put(port, licenseKeys);
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


    public List<String> getLicenseKeyList() {
        return new ArrayList<>(CLIENT_CHANNEL.keySet());
    }


}
