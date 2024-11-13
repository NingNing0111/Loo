package me.pgthinker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Project: me.pgthinker.enums
 * @Author: De Ning
 * @Date: 2024/10/7 18:46
 * @Description:
 */
@RequiredArgsConstructor
@Getter
public enum ProtocolEnum {

    TCP("tcp"),
    UDP("udp");

    private final String value;

    private final static Map<String, ProtocolEnum> cache = Stream.of(values()).collect(Collectors.toMap(ProtocolEnum::getValue, Function.identity()));

    public static ProtocolEnum of(String value) {
        return cache.get(value);
    }
}
