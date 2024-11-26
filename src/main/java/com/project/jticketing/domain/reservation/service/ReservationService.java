package com.project.jticketing.domain.reservation.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.dto.response.ReservationResponseDTO;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final EventRepository eventRepository; // Event 정보를 가져오기 위해 필요
	private final UserRepository userRepository; // User 정보를 가져오기 위해 필요

	@Transactional
	public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO, Long concertId, String day, UserDetailsImpl authUser) {
		User currentUser = userRepository.findById(authUser.getUser().getId())
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

		Event concertEvent = eventRepository.findByConcertIdAndConcertDate(concertId, LocalDate.parse(day))
			.orElseThrow(() -> new IllegalArgumentException("해당 날짜에 일치하는 콘서트 이벤트가 없습니다."));

		boolean isSeatAlreadyReserved = reservationRepository.existsBySeatNumAndConcertEventId(requestDTO.getSeatNum(), concertEvent.getId());
		if (isSeatAlreadyReserved) {
			throw new IllegalArgumentException("해당 좌석은 이미 예약되었습니다.");
		}

		Reservation reservation = Reservation.builder()
			.seatNum(requestDTO.getSeatNum())
			.reservationDate(LocalDate.now())
			.user(currentUser)
			.concertEvent(concertEvent.getConcert())
			.build();

		reservationRepository.save(reservation);

		return ReservationResponseDTO.builder()
			.reservationId(reservation.getId())
			.seatNum(reservation.getSeatNum())
			.reservationDate(reservation.getReservationDate())
			.concertId(concertId)
			.concertDate(concertEvent.getConcertDate())
			.build();
	}
}
