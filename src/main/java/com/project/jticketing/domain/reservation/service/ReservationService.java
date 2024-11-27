package com.project.jticketing.domain.reservation.service;

import com.project.jticketing.config.redis.LockService;
import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final LockService lockService;

    @Transactional
    public void bookSeat(UserDetailsImpl userDetails, Long eventId, Long seatNum) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        reservationRepository.findByEventAndSeatNum(event, seatNum)
                .ifPresent(existingReservation -> {
                    throw new RuntimeException("이미 예약된 자리입니다.");
                });

        Reservation reservation = new Reservation(
                seatNum,
                LocalDateTime.now(),
                userDetails.getUser(),
                event
        );
        reservationRepository.save(reservation);
    }

    @Transactional
    public void bookSeatRedis(UserDetailsImpl userDetails, Long eventId, Long seatNum) {
        String lockKey = "reservation:" + eventId + ":" + seatNum;
        String lockValue = UUID.randomUUID().toString();
        long lockTtl = 5000;

        try {
            if (!lockService.tryLock(lockKey, lockValue, lockTtl)) {
                throw new RuntimeException("좌석 예매의 lock 획득 실패");
            }

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found"));

            boolean isSeatAlreadyReserved = reservationRepository.existsByEventAndSeatNum(event, seatNum);
            if (isSeatAlreadyReserved) {
                throw new RuntimeException("이미 예약된 자리입니다.");
            }

            Reservation reservation = new Reservation(
                    seatNum,
                    LocalDateTime.now(),
                    userDetails.getUser(),
                    event
            );
            reservationRepository.save(reservation);
        } finally {
            lockService.unlock(lockKey, lockValue);
        }
    }
}
