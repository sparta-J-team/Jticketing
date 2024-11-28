package com.project.jticketing.domain.reservation.repository;

import com.project.jticketing.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import jakarta.persistence.LockModeType;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByEventIdAndSeatNum(Long eventId, Long seatNum);

    long countByEventIdAndSeatNum(Long eventId, Long seatNum);

	@Query("SELECT r FROM Reservation r WHERE r.event.id = :eventId AND r.seatNum = :seatNum")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Reservation> findByEventIdAndSeatNumWithLock(Long eventId, Long seatNum);
}
