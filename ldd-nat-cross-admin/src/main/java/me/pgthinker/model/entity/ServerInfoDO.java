package me.pgthinker.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Project: me.pgthinker.model.entity
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/24 23:39
 * @Description:
 */
@TableName("server_info")
@Data
public class ServerInfoDO {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serverId;
    private String serverName;
    private String osName;
    private String osArch;
    private String osVersion;
    private LocalDateTime registerTime;
}
