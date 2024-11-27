package com.project.jticketing.domain.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
import com.project.jticketing.redis.service.LockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final LockService lockService;
	private static final String LOCK_KEY_PREFIX = "reservation_lock:";
	private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

	@Transactional
	public ReservationResponseDTO createReservation(Long seatNum, Long eventId, User user) {
		userRepository.findById(user.getId())
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

		// 콘서트 이벤트 조회 (eventId를 이용하여 해당 콘서트 이벤트를 조회)
		Event concertEvent = eventRepository.findById(eventId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID에 일치하는 콘서트 이벤트가 없습니다."));

		String lockKey = LOCK_KEY_PREFIX + eventId + ":" + seatNum;

		if (lockService.acquireLock(lockKey)) {
		//if (lockKey != null) {
			try {
				// 좌석이 이미 예약되었는지 확인
				boolean isSeatAlreadyReserved = reservationRepository.existsBySeatNumAndEventId(seatNum, concertEvent.getId());
				if (isSeatAlreadyReserved) {
					throw new IllegalArgumentException("해당 좌석은 이미 예약되었습니다.");
				}

				// 예약 생성
				Reservation reservation = new Reservation();
				reservation.setSeatNum(seatNum);
				reservation.setReservationDate(LocalDate.now());
				reservation.setUser(user);
				reservation.setEvent(concertEvent);
				reservationRepository.save(reservation);
				log.info("Reservation saved with ID: {}", reservation.getId());

				// ReservationResponseDTO 빌더 패턴으로 생성하여 반환
				return ReservationResponseDTO.builder()
					.reservationId(reservation.getId())
					.seatNum(reservation.getSeatNum())
					.reservationDate(reservation.getReservationDate())
					.concertId(concertEvent.getConcert().getId()) // 콘서트 ID를 DTO에 포함
					.concertDate(concertEvent.getConcertDate())  // 콘서트 날짜 포함
					.build();
			} finally {
				// 예약 처리 후 락 해제
				lockService.releaseLock(lockKey);
			}
		} else {
			// 락을 얻지 못한 경우 예약 실패 처리
			throw new IllegalStateException("현재 예약 처리가 진행 중입니다. 잠시 후 다시 시도해주세요.");
		}



		// boolean isSeatAlreadyReserved = reservationRepository.existsBySeatNumAndEventId(seatNum, concertEvent.getId());
		// if (isSeatAlreadyReserved) {
		// 	throw new IllegalArgumentException("해당 좌석은 이미 예약되었습니다.");
		// }
		//
		// Reservation reservation = new Reservation();
		// reservation.setSeatNum(seatNum);
		// reservation.setReservationDate(LocalDate.now());
		// reservation.setUser(user);
		// reservation.setEvent(concertEvent);
		// reservationRepository.save(reservation);
		//
		// // ReservationResponseDTO 빌더 패턴으로 생성하여 반환
		// return ReservationResponseDTO.builder()
		// 	.reservationId(reservation.getId())
		// 	.seatNum(reservation.getSeatNum())
		// 	.reservationDate(reservation.getReservationDate())
		// 	.concertId(concertEvent.getConcert().getId()) // 콘서트 ID를 DTO에 포함
		// 	.concertDate(concertEvent.getConcertDate())  // 콘서트 날짜 포함
		// 	.build();
	}
}
