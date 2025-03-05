package me.pgthinker.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.pgthinker.admin.model.BaseDO;

import java.io.Serializable;
import java.util.List;

/**
 * @Project: me.pgthinker.model.entity
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/4 17:11
 * @Description:
 */
@TableName(value = "visitor_config",autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class VisitorConfigDO extends BaseDO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer type; // 配置类型
    private String serverName; // 服务名称 可空
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> blackList; // 黑名单
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> whiteList; // 白名单
}
