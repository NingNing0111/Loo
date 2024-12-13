package me.pgthinker.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Project: me.pgthinker.model.vo
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 16:44
 * @Description:
 */
@Data
public class ServerInfoVO {
    private String id;
    private String serverId;
    private String serverName;
    private String osName;
    private String osArch;
    private String osVersion;
    private LocalDateTime registerTime;
    private String ip;
    private String hostname;
    private Integer port;
    /**
     * 是否存活
     */
    private Boolean isLive;
}
