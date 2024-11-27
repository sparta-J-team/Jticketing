package com.project.jticketing.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockService {
    private final LockRedisRepository lockRedisRepository;

    public boolean tryLock(String key, String value, long ttl) {
        return lockRedisRepository.acquireLock(key, value, ttl);
    }

    public void unlock(String key, String value) {
        lockRedisRepository.releaseLock(key, value);
    }
}
