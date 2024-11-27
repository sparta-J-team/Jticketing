package com.project.jticketing.domain.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
	public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO, Long eventId, User user) {
		// 현재 사용자를 가져오기
		userRepository.findById(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

		// 콘서트 이벤트 조회 (eventId를 이용하여 해당 콘서트 이벤트를 조회)
		Event concertEvent = eventRepository.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID에 일치하는 콘서트 이벤트가 없습니다."));

		// 해당 좌석이 이미 예약되었는지 확인
		boolean isSeatAlreadyReserved = reservationRepository.existsBySeatNumAndEventId(requestDTO.getSeatNum(), concertEvent.getId());
		if (isSeatAlreadyReserved) {
			throw new IllegalArgumentException("해당 좌석은 이미 예약되었습니다.");
		}

		// 데이터베이스에 예약 저장
		Reservation reservation = new Reservation();
		reservation.setSeatNum(requestDTO.getSeatNum());
		reservation.setReservationDate(LocalDate.now());
		reservation.setUser(user);
		reservation.setEvent(concertEvent); // concertEvent를 설정하여 event_id가 자동으로 설정되도록 함
		reservationRepository.save(reservation);

		// ReservationResponseDTO 빌더 패턴으로 생성하여 반환
		return ReservationResponseDTO.builder()
			.reservationId(reservation.getId())
			.seatNum(reservation.getSeatNum())
			.reservationDate(reservation.getReservationDate())
			.concertId(concertEvent.getConcert().getId()) // 콘서트 ID를 DTO에 포함
			.concertDate(concertEvent.getConcertDate())  // 콘서트 날짜 포함
			.build();
	}
}
