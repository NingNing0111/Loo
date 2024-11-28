package me.pgthinker.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
public class ServerSystemInfoDO implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String serverId;
    private Long maxMemory;
    private Long totalMemory;
    private Long usableMemory;
    private Long freeMemory;
    private LocalDateTime registerTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
