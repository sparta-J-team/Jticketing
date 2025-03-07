package com.project.jticketing.domain.reservation.service;

import com.project.jticketing.config.redis.LockService;
import com.project.jticketing.config.redis.RedissonLockService;
import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final LockService lockService;

    private final EventRepository eventRepository;

    private final RedissonLockService redissonLockService;

    @Transactional
    public boolean reserveSeatWithoutRedis(UserDetailsImpl authUser, Long eventId, Long seatNum) {
        // DB에서 해당 좌석의 예약 상태 확인
        Optional<Reservation> dbReservationOpt = reservationRepository.findByEventIdAndSeatNum(eventId, seatNum);

        // 이미 예약된 좌석은 예약 불가
        if (dbReservationOpt.isPresent()) {
            return false;
        }

        // 데이터베이스에 예약 저장
        Reservation reservation = new Reservation();
        reservation.setSeatNum(seatNum);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setUser(authUser.getUser());
        reservation.setEvent(eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found")));
        reservationRepository.save(reservation);

        return true;
    }

    public boolean reserveSeatWithRedis(UserDetailsImpl authUser, Long eventId, Long seatNum) {
        String redisKey = "event:" + eventId + ":seat:" + seatNum;
        if (lockService.tryLock(redisKey)) {
            System.out.println("Locking흭득 - " + redisKey);
            boolean result = reserveSeatWithoutRedis(authUser, eventId, seatNum);
            System.out.println("Locking해제 - " + redisKey);
            lockService.unlock(redisKey);
            return result;
        }
        return false;
    }

    public boolean reserveSeatWithRedisWithAop(UserDetailsImpl authUser, Long eventId, Long seatNum) {
        return reserveSeatWithoutRedis(authUser, eventId, seatNum);
    }


    @Transactional
    public Boolean exclusiveLock(User user, Long eventId, long seatNum) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // 동시성 문제 해결을 위해 findOne with Lock
        Optional<Reservation> existingReservation = reservationRepository
                .findByEventAndSeatNum(event, seatNum);

        if (existingReservation.isPresent()) {
            throw new IllegalStateException("해당 좌석에 대한 예매가 진행 중입니다");
        }

        Reservation reservation = new Reservation(seatNum, LocalDateTime.now(), user, event);
        reservationRepository.save(reservation);
        return true;
    }

    //Redisson을 통해 Lock 구현
    public boolean reserveSeatWithRedisson(UserDetailsImpl authUser, Long eventId, Long seatNum) {
        String lockKey = "event:" + eventId + ":seat:" + seatNum;

        try {
            // Try to acquire lock with 5 seconds wait time and 3 seconds lease time
            if (redissonLockService.tryLock(lockKey, 5000, 3000)) {
                try {
                    return reserveSeatWithoutRedis(authUser, eventId, seatNum);
                } finally {
                    redissonLockService.unlock(lockKey);
                }
            }
            return false;
        } catch (Exception e) {
            // Handle any unexpected exceptions
            return false;
        }
    }


}
