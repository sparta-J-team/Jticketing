package com.project.jticketing.redis.service;

import org.springframework.stereotype.Service;

import com.project.jticketing.redis.repository.LockRedisRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LockService {

	private final LockRedisRepository lockRedisRepository;

	// Lock을 획득하는 메서드
	public boolean acquireLock(String lockKey) {
		return lockRedisRepository.lock(lockKey); // 락 획득
	}

	// Lock을 해제하는 메서드
	public void releaseLock(String lockKey) {
		lockRedisRepository.unlock(lockKey); // 락 해제
	}
}
