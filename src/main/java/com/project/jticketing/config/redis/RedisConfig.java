package com.project.jticketing.config.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean // redis 에 연결하기 위한 객체 생성
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379"); // Redis URL 설정
    }

    @Bean // redis 서버와의 상태 기반 연결,  Stateful (상태유지). 클라이언트-서버 관계에서 서버가 클라이언트의 상태를 보존함을 의미
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean // Redis 서버에서 동기식 명령을 수행하기 위한 인터페이스를 제공
    public RedisCommands<String, String> redisCommands(StatefulRedisConnection<String, String> connection) {
        return connection.sync(); // 동기식 명령 실행 객체를 반환
    }

    @Bean // RedisLock 객체를 생성하여 Redis를 활용한 분산 락 구현
    public RedisLock redisLock(RedisCommands<String, String> redisCommands) {
        return new RedisLock(redisCommands);
    }
}

