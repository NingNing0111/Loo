package me.pgthinker.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Project: me.pgthinker.model.vo
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/1 15:45
 * @Description:
 */
@Data
public class PageBaseVO implements Serializable {
    private int page = 0;
    private int pageSize = 10;
}
