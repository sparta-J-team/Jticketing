package com.project.jticketing.config.redis;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LockRedisRepository {
    private final StatefulRedisConnection<String, String> connection;

    public boolean acquireLock(String key, String value, long ttlMillis) {
        RedisCommands<String, String> commands = connection.sync();
        String result = commands.set(key, value, SetArgs.Builder.nx().px(ttlMillis));
        return "OK".equals(result);
    }

    public void releaseLock(String key, String value) {
        RedisCommands<String, String> commands = connection.sync();
        // Use a Lua script to ensure atomic check-and-delete
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        commands.eval(script, ScriptOutputType.INTEGER, new String[]{key}, value);
    }
}