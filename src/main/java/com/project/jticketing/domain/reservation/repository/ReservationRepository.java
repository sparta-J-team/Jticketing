package com.project.jticketing.domain.reservation.repository;

import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByEventAndSeatNum(Event event, Long seatNum);

    boolean existsByEventAndSeatNum(Event event, Long seatNum);
}
