package me.pgthinker.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @Project: me.pgthinker.util
 * @Author: NingNing0111
 * @Github: https://github.com/ningning0111
 * @Date: 2024/11/14 13:18
 * @Description:
 */
public class HttpResponseUtil {
    /**
     * HTTP/1.1 200
     * Content-Type: text/html;charset=UTF-8
     * Content-Length: 11
     * Date: Thu, 14 Nov 2024 05:13:45 GMT
     * Keep-Alive: timeout=60
     * Connection: keep-alive
     * @param resStr
     * @return
     */
    public static Map<String,String> parseMap(String resStr){
        Map<String, String> responseHeaders = new HashMap<>();

        // 按行分割字符串
        String[] lines = resStr.split("\\r?\\n");

        boolean isHeader = true;  // 标记当前是否处于头部解析阶段
        StringBuilder responseBody = new StringBuilder(); // 响应体的构建器

        for (String line : lines) {
            // 忽略空行
            if (line.trim().isEmpty()) {
                isHeader = false; // 遇到空行后，开始解析响应体
                continue;
            }

            if (isHeader) {
                // 处理每一行，分割键值对
                int index = line.indexOf(":");
                if (index > 0) {
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index + 1).trim();
                    responseHeaders.put(key, value);
                }
            } else {
                // 处理响应体
                responseBody.append(line).append(System.lineSeparator());
            }
        }

        // 可选：添加响应体到 Map 中（如果需要）
        responseHeaders.put("Response-Body", responseBody.toString().trim());

        return responseHeaders;
    }
}
