package me.pgthinker.admin.vo;

import lombok.Data;

/**
 * @Project: me.pgthinker.admin.vo
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/4 17:41
 * @Description:
 */
@Data
public class ServerClientVO {
    // 服务端ID
    private String serverId;
    // 接入的客户端主机地址
    private String clientHost;
    // 接入的客户端端口
    private Integer clientPort;
    // 授权码
    private String licenseKey;
}
