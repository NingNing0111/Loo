package me.pgthinker.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Project: me.pgthinker.model.vo
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 16:44
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfoVO extends PageBaseVO{
    private String id;
    private String serverName;
    private String osName;
    private String osArch;
    private String osVersion;
    private String serverHost;
    private Integer serverPort;

    private LocalDateTime registerTime;
    private Long liveClientCnt;

    /**
     * 是否存活
     */
    private Boolean isLive;
}
