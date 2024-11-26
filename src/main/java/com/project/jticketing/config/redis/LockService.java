package com.project.jticketing.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LockService {

    private final RedisLockRepository redisLockRepository;

    // 락을 시도
    public boolean tryLock(String key) {
        return redisLockRepository.lock(key);
    }

    // 락 해제
    public void unlock(String key) {
        redisLockRepository.unlock(key);
    }

    // 상태 조회
    public String getStatus(String key) {
        return redisLockRepository.getStatus(key);
    }

    // 상태 설정
    public void setStatus(String key, String status) {
        redisLockRepository.setStatus(key, status);
    }
}
