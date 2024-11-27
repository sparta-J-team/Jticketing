package com.project.jticketing.domain.reservation.repository;

import java.util.List;

import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	//boolean existsBySeatNumAndConcertEventId(Long seatNum, Long concertId);

	boolean existsBySeatNumAndEventId(Long seatNum, Long id);
	List<Reservation> findBySeatNumAndEventId(Long seatNum, Long eventId);
}
