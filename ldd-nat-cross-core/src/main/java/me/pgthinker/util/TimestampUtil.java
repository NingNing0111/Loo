package me.pgthinker.util;

import com.google.protobuf.Timestamp;

import java.time.Instant;

/**
 * @Project: me.pgthinker.util
 * @Author: De Ning
 * @Date: 2024/10/7 19:21
 * @Description:
 */
public class TimestampUtil {

    /**
     * 当前时间戳
     * @return
     */
    public static Timestamp now() {
        Instant now = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }
}
