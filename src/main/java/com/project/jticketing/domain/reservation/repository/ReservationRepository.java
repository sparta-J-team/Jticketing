package com.project.jticketing.domain.reservation.repository;

import com.project.jticketing.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
