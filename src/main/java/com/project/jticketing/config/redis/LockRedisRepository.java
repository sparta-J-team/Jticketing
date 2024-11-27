package com.project.jticketing.config.redis;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockRedisRepository {
    private final StatefulRedisConnection<String, String> connection;

    public boolean acquireLock(String key, String value, long ttl) {
        RedisCommands<String, String> commands = connection.sync();
        String result = commands.set(key, value);
        return "OK".equals(result);
    }

    public void releaseLock(String key, String value) {
        RedisCommands<String, String> commands = connection.sync();
        String currentValue = commands.get(key);
        if (value.equals(currentValue)) {
            commands.del(key);
        }
    }
}