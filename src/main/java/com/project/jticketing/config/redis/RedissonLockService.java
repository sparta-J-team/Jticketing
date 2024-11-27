package com.project.jticketing.config.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonLockService{
    private final RedissonClient redissonClient;

    public boolean tryLock(String key) {
        RLock lock = redissonClient.getLock(key);
        try {
            // 락을 5초 동안 획득 시도, 락 유지 시간은 10초로 설정
            return lock.tryLock(5, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

}