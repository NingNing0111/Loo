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
 * @Date: 2024/11/24 23:39
 * @Description: 注册的服务信息
 */
@TableName("server_info")
@Data
@EqualsAndHashCode(callSuper = false)
public class ServerInfoDO extends BaseDO implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serverName;
    private String osName;
    private String osArch;
    private String osVersion;
    private String serverHost;
    private Integer serverPort;

    private LocalDateTime registerTime;
    private Boolean isLive; // 是否存活

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
