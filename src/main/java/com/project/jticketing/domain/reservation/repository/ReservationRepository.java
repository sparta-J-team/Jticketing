package com.project.jticketing.domain.reservation.repository;

import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByEventIdAndSeatNum(Long eventId, Long seatNum);

    long countByEventIdAndSeatNum(Long eventId, Long seatNum);


    @Query("select r from Reservation r where r.event = :event and r.seatNum = :seatNum")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findByEventAndSeatNum(Event event, long seatNum);
}
