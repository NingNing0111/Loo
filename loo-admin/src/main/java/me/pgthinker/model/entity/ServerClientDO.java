package me.pgthinker.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.pgthinker.admin.model.BaseDO;

import java.io.Serializable;

/**
 * @Project: me.pgthinker.model.entity
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/4 17:01
 * @Description: 一个服务端对应多个客户端 清除服务端上的licenseKey 意味着客户端断开
 */
@TableName("server_client")
@Data
@EqualsAndHashCode(callSuper = false)
public class ServerClientDO extends BaseDO implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    // 服务端ID
    private String serverId;
    // 接入的客户端主机地址
    private String clientHost;
    // 接入的客户端端口
    private Integer clientPort;
    // 授权码
    private String licenseKey;
    // 是否存活 默认为false
    private Boolean isLive;
}
