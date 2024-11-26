package com.project.jticketing.config.redis;


import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.UUID;

public class RedisLock {
    private final RedisCommands<String, String> redisCommands;

    public RedisLock(RedisCommands<String, String> redisCommands) {
        this.redisCommands = redisCommands;
    }

    public String acquireLock(String lockKey, long expireTimeMillis) {
        String lockValue = UUID.randomUUID().toString(); // 고유 값 생성
        String result = redisCommands.set(
                lockKey,
                lockValue,
                SetArgs.Builder.nx().px(expireTimeMillis)
        );
        return "OK".equals(result) ? lockValue : null; // 성공 시 lockValue 반환
    }

    public boolean releaseLock(String lockKey, String lockValue) {
        String luaScript =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) " +
                        "else return 0 end";

        Long result = redisCommands.eval(luaScript, ScriptOutputType.INTEGER, new String[]{lockKey}, lockValue);
        return result != null && result > 0;
    }
}
