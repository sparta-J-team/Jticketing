package com.project.jticketing.domain.reservation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.service.ReservationService;
import com.project.jticketing.domain.reservation.dto.response.ReservationResponseDTO;
import com.project.jticketing.domain.reservation.entity.Reservation;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/concert")
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;

	@PostMapping("/{concertId}/{day}")
	public ResponseEntity<ReservationResponseDTO> createReservation(
		@RequestBody ReservationRequestDTO requestDTO,
		@PathVariable Long concertId,
		@PathVariable String day,
		@AuthenticationPrincipal UserDetailsImpl authUser
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(requestDTO, concertId, day, authUser));
	}
}
