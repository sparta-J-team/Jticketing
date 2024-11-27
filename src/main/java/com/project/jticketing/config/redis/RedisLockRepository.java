package com.project.jticketing.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class RedisLockRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(String key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key, "lock", Duration.ofMillis(3000));
    }

    public Boolean unlock(String key) {
        return redisTemplate.delete(key.toString());
    }

    // 상태 조회
    public String getStatus(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 상태 설정
    public void setStatus(String key, String status) {
        redisTemplate.opsForValue().set(key, status);
    }
}