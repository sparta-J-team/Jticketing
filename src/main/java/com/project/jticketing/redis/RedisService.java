package com.project.jticketing.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	public RedisService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// 데이터 저장
	public void save(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public String find(String key) {
		Object value = redisTemplate.opsForValue().get(key);
		return value != null ? value.toString() : "No value found for key: " + key;
	}

	// 데이터 삭제
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	// 연결 상태 확인
	public String testConnection() {
		try {
			// Redis에 간단한 명령어 실행하여 연결 확인
			redisTemplate.getConnectionFactory().getConnection().ping();
			return "Redis is connected!";
		} catch (Exception e) {
			return "Redis connection failed: " + e.getMessage();
		}
	}
}
