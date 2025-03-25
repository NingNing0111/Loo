package me.pgthinker.admin.vo;

import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;

/**
 * @Project: me.pgthinker.admin
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/2 14:43
 * @Description:
 */
@Data
@AllArgsConstructor
public class HeartbeatDataVO implements Serializable {
    // JVM内存信息
    private Long jvmMaxMemory;
    private Long jvmTotalMemory;
    private Long jvmUsableMemory;
    private Long jvmFreeMemory;

    // CPU 相关
    private Double cpuUsage; // CPU 使用率 (%)
    private Double systemLoad; // 系统负载
    private Integer cpuCores;

    // 线程信息
    private Integer threadCount; // 线程数

    // GC 信息
    private Long gcCount; // GC 总次数
    private Long gcTime; // GC 总耗时 (ms)

    // 磁盘信息
    private Long diskTotal; // 磁盘总容量 (MB)
    private Long diskFree; // 磁盘剩余容量 (MB)


    public HeartbeatDataVO() {
        RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
        this.jvmMaxMemory = runtimeInfo.getMaxMemory();
        this.jvmTotalMemory = runtimeInfo.getTotalMemory();
        this.jvmFreeMemory = runtimeInfo.getFreeMemory();
        this.jvmUsableMemory = runtimeInfo.getUsableMemory();

        this.cpuUsage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        this.systemLoad = osBean.getSystemLoadAverage();
        this.cpuCores = osBean.getAvailableProcessors();
        this.threadCount = SystemUtil.getTotalThreadCount();

        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        long gcCount = 0, gcTime = 0;
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            gcCount += gcBean.getCollectionCount();
            gcTime += gcBean.getCollectionTime();
        }
        this.gcCount = gcCount;
        this.gcTime = gcTime;

        File root = new File("/");
        long totalSpace = root.getTotalSpace(); // MB
        long freeSpace = root.getFreeSpace(); // MB

        this.diskTotal = totalSpace;
        this.diskFree = freeSpace;
    }
    private static final long serialVersionUID = 1L;

}
