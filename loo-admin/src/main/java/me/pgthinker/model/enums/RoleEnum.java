package me.pgthinker.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Project: me.pgthinker.model.enums
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/25 16:18
 * @Description:
 */
@RequiredArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN("admin"),
    USER("user");
    private final String name;
}
