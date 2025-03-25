package me.pgthinker.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @Project: me.pgthinker.admin.vo
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/4 17:30
 * @Description:
 */
@Data
public class VisitorConfigVO {
    private Integer type; // 配置类型
    private String serverName; // 服务名称 可空
    private List<String> blackList; // 黑名单
    private List<String> whiteList; // 白名单
}
