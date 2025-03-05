package me.pgthinker.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.pgthinker.admin.model.BaseDO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Project: me.pgthinker.model.entity
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:42
 * @Description:
 */
@TableName("server_system_info")
@Data
@EqualsAndHashCode(callSuper = false)

public class ServerSystemInfoDO extends BaseDO implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String serverId;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
