package me.pgthinker.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project: me.pgthinker.model.enums
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2025/3/4 17:14
 * @Description:
 */
@RequiredArgsConstructor
@Getter
public enum ConfigTypeEnum {
    ADMIN("管理端", 1),
    SERVER("服务端", 2),
    CLIENT("客户端",3);
    private final String label;
    private final Integer value;

    private final static Map<Integer, ConfigTypeEnum> DATA_MAP = new HashMap<>();

    static {
        for (ConfigTypeEnum enumValue : ConfigTypeEnum.values()) {
            DATA_MAP.put(enumValue.getValue(), enumValue);
        }
    }

    public static ConfigTypeEnum getByValue(Integer value) {
        return DATA_MAP.get(value);
    }
}
