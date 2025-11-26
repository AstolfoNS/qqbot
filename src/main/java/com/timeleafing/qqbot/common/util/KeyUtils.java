package com.timeleafing.qqbot.common.util;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Redis 缓存 Key 生成工具类
 * 用于统一规范 key 命名，避免不同模块冲突
 * 示例：
 *   CacheKeyUtils.user("login", "20230001")
 *   => "user:login:20230001"
 *   CacheKeyUtils.of("academic-festival", "guest", "list")
 *   => "academic-festival:guest:list"
 */

public final class KeyUtils {

    private static final String DELIMITER = ":";


    private KeyUtils() {}

    /** 通用 Key 生成 */
    public static String of(String... parts) {
        if (parts == null || parts.length == 0) {
            throw new IllegalArgumentException("Cache key 片段不能为空");
        }
        String key = Arrays
                .stream(parts)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(DELIMITER));

        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Cache key 不能为空");
        }
        return key;
    }

    /** 通用模块前缀包装器 */
    private static String[] withPrefix(String prefix, String... parts) {
        String[] combined = new String[parts.length + 1];

        combined[0] = prefix;

        System.arraycopy(parts, 0, combined, 1, parts.length);

        return combined;
    }
}
