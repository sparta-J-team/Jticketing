package com.project.jticketing.config.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonLockService {
    private final RedissonClient redissonClient;

    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        // Only unlock if the current thread holds the lock
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
