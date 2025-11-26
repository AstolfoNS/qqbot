package com.timeleafing.qqbot.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisUtils {

    private final StringRedisTemplate redis;

    private final JsonMapper mapper;

    private static final String NULL_CACHE_PREFIX = "NULL_";

    private static final long DEFAULT_TTL_HOURS = 1;

    private static final long NULL_CACHE_TTL_MINUTES = 5;


    public boolean hasKey(@NotNull String key) {
        validateKey(key);

        return redis.hasKey(key);
    }

    public boolean delete(@NotNull String key) {
        validateKey(key);

        return redis.delete(key);
    }

    public long delete(@NotNull Collection<String> keys) {
        return CollectionUtils.isEmpty(keys) ? 0 : Optional.of(redis.delete(keys)).orElse(0L);
    }

    public void expire(
            @NotNull String key,
            long timeout,
            @NotNull TimeUnit unit
    ) {
        validateKey(key);

        redis.expire(key, timeout, unit);
    }

    public Set<String> keys(@NotNull String pattern) {
        return Optional.of(redis.keys(pattern)).orElse(Collections.emptySet());
    }

    /* -------------------- KV操作 -------------------- */

    public <T> void set(@NotNull String key, @NotNull T value) {
        set(key, value, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
    }

    public <T> void set(
            @NotNull String key,
            @NotNull T value,
            long timeout,
            @NotNull TimeUnit unit
    ) {
        validateKey(key);

        Optional.ofNullable(toJson(value)).ifPresent(json -> redis.opsForValue().set(key, json, timeout, unit));
    }

    @Nullable
    public <T> T get(@NotNull String key, @NotNull Class<T> clazz) {
        validateKey(key);

        String json = redis.opsForValue().get(key);

        return StringUtils.hasText(json) ? fromJson(json, clazz) : null;
    }

    public <T> T getOrSet(@NotNull String key, @NotNull Supplier<T> supplier) {
        return getOrSet(key, supplier, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrSet(
            @NotNull String key,
            @NotNull Supplier<T> supplier,
            long timeout,
            @NotNull TimeUnit unit
    ) {
        Object cached = get(key, Object.class);

        if (cached != null) {
            return (T) cached;
        }
        if (isNullCached(key)) {
            return null;
        }
        try {
            T newValue = supplier.get();

            if (newValue != null) {
                set(key, newValue, timeout, unit);
            } else {
                setNullValueCache(key);
            }
            return newValue;
        } catch (Exception e) {
            log.error("执行 supplier 异常: key={}, error={}", key, e.getMessage(), e);

            return null;
        }
    }

    /* -------------------- List操作 -------------------- */

    public <T> long setList(@NotNull String key, @NotNull List<T> list) {
        validateKey(key);

        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        List<String> jsonList = list
                .stream()
                .map(this::toJson)
                .filter(Objects::nonNull)
                .toList();

        redis.opsForList().rightPushAll(key, jsonList);

        return jsonList.size();
    }

    @Nullable
    public <T> List<T> getList(@NotNull String key, @NotNull Class<T> clazz) {
        validateKey(key);

        List<String> jsonList = redis.opsForList().range(key, 0, -1);

        if (jsonList == null) {
            return Collections.emptyList();
        }
        return jsonList
                .stream()
                .map(json -> fromJson(json, clazz))
                .filter(Objects::nonNull)
                .toList();
    }

    /* -------------------- Set操作 -------------------- */

    public <T> long setSet(@NotNull String key, @NotNull Set<T> set) {
        validateKey(key);

        if (CollectionUtils.isEmpty(set)) {
            return 0;
        }
        String[] jsons = set
                .stream()
                .map(this::toJson)
                .filter(Objects::nonNull)
                .distinct()
                .toArray(String[]::new);

        return Optional.ofNullable(redis.opsForSet().add(key, jsons)).orElse(0L);
    }

    @Nullable
    public <T> Set<T> getSet(@NotNull String key, @NotNull Class<T> clazz) {
        validateKey(key);

        Set<String> jsonSet = redis.opsForSet().members(key);

        if (jsonSet == null) {
            return Collections.emptySet();
        }
        return jsonSet
                .stream()
                .map(json -> fromJson(json, clazz))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /* -------------------- Hash操作 -------------------- */

    public <T> void putHash(
            @NotNull String key,
            @NotNull String hashKey,
            @NotNull T value
    ) {
        validateKey(key);
        validateKey(hashKey);

        Optional.ofNullable(toJson(value)).ifPresent(json -> redis.opsForHash().put(key, hashKey, json));
    }

    @Nullable
    public <T> T getHashValue(@NotNull String key, @NotNull String hashKey, @NotNull Class<T> clazz) {
        validateKey(key);
        validateKey(hashKey);

        Object json = redis.opsForHash().get(key, hashKey);

        return json == null ? null : fromJson(json.toString(), clazz);
    }

    public void deleteHashKey(@NotNull String key, @NotNull String hashKey) {
        validateKey(key);

        redis.opsForHash().delete(key, hashKey);
    }

    /* -------------------- 内部工具方法 -------------------- */

    private void validateKey(String key) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Redis key 不能为空");
        }
    }

    private void setNullValueCache(@NotNull String key) {
        redis.opsForValue().set(NULL_CACHE_PREFIX.concat(key), "NULL", Duration.ofMinutes(NULL_CACHE_TTL_MINUTES));
    }

    private boolean isNullCached(@NotNull String key) {
        return redis.hasKey(NULL_CACHE_PREFIX.concat(key));
    }

    private <T> String toJson(T value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("序列化失败: {}", e.getMessage(), e);

            return null;
        }
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("反序列化失败: type={}, error={}", clazz.getSimpleName(), e.getMessage(), e);

            return null;
        }
    }
}
