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
        String lockValue = userDetails.getUser().toString();
        long lockTtl = 5000; // 5 seconds

        if (!lockService.tryLock(lockKey, lockValue, lockTtl)) {
            throw new RuntimeException("Lock 획득 불가");
        }

        try {
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
        } finally {
            lockService.unlock(lockKey, lockValue);
        }
    }
}
