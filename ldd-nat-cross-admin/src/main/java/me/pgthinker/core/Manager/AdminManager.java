package me.pgthinker.core.Manager;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Project: me.pgthinker.core.Manager
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:02
 * @Description:
 */
public class AdminManager {

    // serverName -> Ctx
    private final static Map<String, ChannelHandlerContext> ADMIN_SERVER_CONTEXT = new ConcurrentHashMap<>();

    // 判断ServerName是否已经注册
    public static boolean isRegistered(String serverName) {
        return ADMIN_SERVER_CONTEXT.containsKey(serverName);
    }

    // 注册
    public static void registerServer(String serverName, ChannelHandlerContext ctx) {
        ADMIN_SERVER_CONTEXT.put(serverName, ctx);
    }

    // 移除
    public static void unRegisterServer(String serverName) {
        ADMIN_SERVER_CONTEXT.remove(serverName);
    }

    public static void unRegisterServer(ChannelHandlerContext ctx) {
        for(String key: ADMIN_SERVER_CONTEXT.keySet()) {
            ChannelHandlerContext serverCtx = ADMIN_SERVER_CONTEXT.get(key);
            if(serverCtx.channel().id().equals(ctx.channel().id())) {
                ADMIN_SERVER_CONTEXT.remove(key);
                return;
            }
        }
    }

}
