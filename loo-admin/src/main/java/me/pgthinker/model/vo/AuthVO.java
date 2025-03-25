package me.pgthinker.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Project: me.pgthinker.model.vo
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 16:20
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthVO implements Serializable {
    private String username;
    private String password;
}
