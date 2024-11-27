package com.project.jticketing.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.project.jticketing.domain.concert.entity.Concert;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.dto.response.ReservationResponseDTO;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.enums.UserRole;
import com.project.jticketing.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class) // Mockito를 위한 확장 사용
class ReservationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private EventRepository eventRepository;

	@Mock
	private ReservationRepository reservationRepository;

	@InjectMocks
	private ReservationService reservationService;

	private User user;
	private Event concertEvent;
	private ReservationRequestDTO requestDTO;

	@BeforeEach
	public void setUp() {
		// Mock User 객체 생성
		user = new User();
		user.setId(1L);

		// Mock Event 객체 생성
		concertEvent = new Event();
		concertEvent.setId(1L);
		concertEvent.setConcert(new Concert());  // Concert 객체도 설정 필요
		concertEvent.getConcert().setId(1L);
		concertEvent.setConcertDate(LocalDate.of(2024, 11, 30));

		// Mock ReservationRequestDTO 생성
		requestDTO = new ReservationRequestDTO(1L);
	}

	@Test
	@Transactional
	public void testCreateReservation_Success() {
		// Given: User와 Event 객체가 주어짐
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(eventRepository.findById(concertEvent.getId())).thenReturn(Optional.of(concertEvent));
		when(reservationRepository.existsBySeatNumAndEventId(requestDTO.getSeatNum(), concertEvent.getId())).thenReturn(
			false);

		// When: createReservation 메서드 호출
		ReservationResponseDTO response = reservationService.createReservation(requestDTO, concertEvent.getId(), user);

		// Then: 응답이 예상한 대로 반환되는지 확인
		assertNotNull(response);
		assertEquals(1, response.getSeatNum());
		assertEquals(concertEvent.getId(), response.getConcertId());
		assertEquals(concertEvent.getConcertDate(), response.getConcertDate());
	}

	@Test
	@Transactional
	public void testCreateReservations_Concurrent() throws InterruptedException, ExecutionException {
		// Given: 10개의 예약을 동시에 진행
		int numberOfReservations = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfReservations);

		// Mock 설정
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(eventRepository.findById(concertEvent.getId())).thenReturn(Optional.of(concertEvent));
		when(reservationRepository.existsBySeatNumAndEventId(requestDTO.getSeatNum(), concertEvent.getId())).thenReturn(false);

		// Callable로 예약 작업을 여러 스레드에서 수행
		Callable<ReservationResponseDTO> task = () -> {
			return reservationService.createReservation(requestDTO, concertEvent.getId(), user);
		};

		// 10개의 예약을 동시에 진행
		List<Future<ReservationResponseDTO>> futures = new ArrayList<>();
		for (int i = 0; i < numberOfReservations; i++) {
			futures.add(executorService.submit(task));
		}

		// 성공한 예약 개수를 카운트
		int successfulReservationsCount = 0;

		// 모든 예약 결과 확인
		for (Future<ReservationResponseDTO> future : futures) {
			try {
				ReservationResponseDTO response = future.get();  // 예매 결과 받아오기
				assertNotNull(response);  // 응답이 null이 아님을 확인
				successfulReservationsCount++;  // 성공한 예약 개수 카운트
				// 예약 결과 검증
				assertEquals(1, response.getSeatNum(), "좌석 번호가 1번이어야 합니다.");
				assertEquals(concertEvent.getId(), response.getConcertId(), "콘서트 ID가 일치해야 합니다.");
				assertEquals(concertEvent.getConcertDate(), response.getConcertDate(), "콘서트 날짜가 일치해야 합니다.");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();  // 인터럽트 상태를 복원
				//fail("예약 작업 중 스레드가 인터럽트 되었습니다.");
			} catch (ExecutionException e) {
				//fail("예약 작업 중 예외가 발생했습니다: " + e.getCause().getMessage());
			}
		}

		executorService.shutdown();

		// Then: 성공한 예약의 수가 10개인지 확인
		assertEquals(10, successfulReservationsCount, "성공한 예약의 개수가 10이어야 합니다.");
	}
}
