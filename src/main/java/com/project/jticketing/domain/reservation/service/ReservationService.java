package com.project.jticketing.domain.reservation.service;

import com.project.jticketing.config.redis.RedisLock;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // final 이 붙은 필드만 생성자로 만들어줌.
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final RedisLock redisLock;
    private final String lockKeyPrefix = "reservation:lock:";

    public String reserve(User user, Long eventId, ReservationRequestDTO requestDTO) {
        Long seatNum = requestDTO.getSeatNum();
        String lockKey = lockKeyPrefix + seatNum;
        String lockValue = redisLock.acquireLock(lockKey, 5000); // 5초 유효기간

        if (reservationRepository.existsBySeatNum(seatNum)) {
            throw new IllegalArgumentException("해당 좌석은 예매가 완료되었습니다.");
        }

        if (lockValue == null) {
            throw new IllegalStateException("현재 다른 사용자가 해당 좌석을 예약 중입니다. 다시 시도하세요.");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new IllegalArgumentException("해당 콘서트 정보가 존재하지 않습니다")
        );

        try {
            Reservation reservation = new Reservation(seatNum, user, event);
            reservationRepository.save(reservation);

            return "예매 성공";
        } finally {
            // 락 해제
            redisLock.releaseLock(lockKey, lockValue);
        }
    }
}

// 락을 획득하지 못했을 때의 동작 방식은 정하기 나름,
// 예를 들어 대기, 재시도, 포기 등
