package me.pgthinker.core.manager;

import lombok.extern.slf4j.Slf4j;
import me.pgthinker.core.handler.LocalProxyHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Project: me.pgthinker.manager
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/12 04:27
 * @Description:
 */
@Slf4j
public class ClientManager {
    private final static Map<String, LocalProxyHandler> LOCAL_PROXY_CHANNEL = new ConcurrentHashMap<>();

    public static void setLocalProxyChannel(String visitor, LocalProxyHandler handler) {
        LOCAL_PROXY_CHANNEL.put(visitor, handler);
    }

    public static LocalProxyHandler getLocalProxyChannel(String visitor){
        return LOCAL_PROXY_CHANNEL.get(visitor);
    }

    public static void removeLocalProxyChannel(String visitor) {
        LOCAL_PROXY_CHANNEL.remove(visitor);
    }

}
