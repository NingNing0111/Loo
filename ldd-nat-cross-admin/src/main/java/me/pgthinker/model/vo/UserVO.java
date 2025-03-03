package me.pgthinker.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Project: me.pgthinker.model.vo
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/1 17:11
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserVO extends PageBaseVO {
    private Long id;
    private String username;
    private String password;
    private String role;
}
