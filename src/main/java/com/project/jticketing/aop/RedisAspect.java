package com.project.jticketing.aop;

import com.project.jticketing.config.redis.LockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisAspect {

    private final LockService lockService;

    @Pointcut("execution(* com.project.jticketing.domain.reservation.service.ReservationService.reserveSeatWithRedisWithAop(..))")
    public void redisPointcut() {}

    @Around("redisPointcut()")
    public Object handleRedisLock(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long eventId = (Long) args[1];
        Long seatNum = (Long) args[2];
        String redisKey = "event:" + eventId + ":seat:" + seatNum;

        if (lockService.tryLock(redisKey)) {
            try {
                return joinPoint.proceed();
            } finally {
                lockService.unlock(redisKey);
            }
        } else {
            return false;
        }
    }
}
