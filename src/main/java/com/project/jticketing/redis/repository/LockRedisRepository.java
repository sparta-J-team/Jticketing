package com.project.jticketing.redis.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LockRedisRepository {
	private final RedisTemplate<String, String> redisTemplate;

	// Redis에서 락을 획득하는 메서드
	public boolean lock(String key) {
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		return Boolean.TRUE.equals(ops.setIfAbsent(key, "locked", Duration.ofMillis(3000))); // 락 성공 여부 반환
	}

	// Redis에서 락을 해제하는 메서드
	public void unlock(String key) {
		redisTemplate.delete(key); // 락 해제
	}
}
