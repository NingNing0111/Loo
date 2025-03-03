package me.pgthinker.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Project: me.pgthinker.model.vo
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/3 12:11
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisDataVO implements Serializable {
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

    private LocalDateTime registerTime;
}
