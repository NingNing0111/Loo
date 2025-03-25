package me.pgthinker.admin.common;

/**
 * @Project: me.pgthinker.admin.common
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 22:01
 * @Description:
 */
public interface AdminConstants {
    String SERVER_NAME = "serverName";

    // 注册信息
    String OS_NAME = "os.name";
    String OS_VERSION = "os.version";
    String OS_ARCH = "os.arch";

    // 心跳数据包传输的信息
    // JVM（Java Virtual Machine）能够使用的最大内存量
    String RUNTIME_MAX_MEMORY = "maxMemory";
    // JVM当前已分配的内存总量
    String RUNTIME_TOTAL_MEMORY = "totalMemory";
    // 当前JVM已分配内存中未被使用的可用内存量
    String RUNTIME_FREE_MEMORY = "freeMemory";
    // 系统中的可用内存
    String RUNTIME_USABLE_MEMORY = "usableMemory";
}
