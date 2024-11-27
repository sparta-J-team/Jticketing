package com.project.jticketing.domain.reservation.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
import com.project.jticketing.redis.service.LockService;

@SpringBootTest
//@ExtendWith(MockitoExtension.class) // Mockito를 위한 확장 사용
class ReservationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired // Redis Lock 관련 서비스는 실제 구현을 사용
	private LockService lockService;

	@Autowired
	private ReservationService reservationService;

	private User user;
	private Event concertEvent;
	private ReservationRequestDTO requestDTO;
	private List<User> users;

	private AtomicInteger userIndex = new AtomicInteger(0);

	@BeforeEach
	public void setUp() {
		users = new ArrayList<>();
		for (long i = 1; i <= 10; i++) {
			UserRole userRole = (i % 2 == 0) ? UserRole.ADMIN : UserRole.USER;  // 유저 역할을 번갈아 설정
			User user = new User(
				"user" + i + "@example.com",  // 이메일
				"password" + i,               // 비밀번호
				"nickname" + i,               // 닉네임
				"주소 " + i,                  // 주소
				"010-1234-567" + i,           // 전화번호
				userRole                      // 유저 역할
			);
			user.setId(i);  // 유저 ID는 1L부터 10L까지 설정
			users.add(user);
		}

		// Mock Event 객체 생성
		concertEvent = new Event();
		concertEvent.setId(1L);
		concertEvent.setConcert(new Concert());  // Concert 객체도 설정 필요
		concertEvent.getConcert().setId(1L);
		concertEvent.setConcertDate(LocalDate.of(2024, 11, 30));
	}

	@Test
	@DisplayName("예매 단건")
	@Transactional
	public void testCreateReservation_Success() {
		// Given: User와 Event 객체가 주어짐
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(eventRepository.findById(concertEvent.getId())).thenReturn(Optional.of(concertEvent));
		when(reservationRepository.existsBySeatNumAndEventId(requestDTO.getSeatNum(), concertEvent.getId())).thenReturn(
			false);

		// When: createReservation 메서드 호출
		ReservationResponseDTO response = reservationService.createReservation(1L, concertEvent.getId(), user);

		// Then: 응답이 예상한 대로 반환되는지 확인
		assertNotNull(response);
		assertEquals(1, response.getSeatNum());
		assertEquals(concertEvent.getId(), response.getConcertId());
		assertEquals(concertEvent.getConcertDate(), response.getConcertDate());
	}

	@Test
	@DisplayName("동시성 제어 적용x, 10개 동시 예매 10개 성공")
	@Transactional
	public void testCreateReservations_Concurrent() {
		// Given: 10개의 예약을 동시에 진행
		int numberOfReservations = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfReservations);

		// Mock 설정
		for (User user : users) {
			when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		}
		when(eventRepository.findById(concertEvent.getId())).thenReturn(Optional.of(concertEvent));
		when(reservationRepository.existsBySeatNumAndEventId(1L, concertEvent.getId())).thenReturn(false);

		// 예약을 수행하는 Callable 작업 생성
		Callable<ReservationResponseDTO> task = () -> {
			// 예약을 진행할 유저 선택 (각 스레드마다 다른 유저)
			User user = users.get((int) (Thread.currentThread().getId() % users.size()));  // Thread ID로 유저 선택
			return reservationService.createReservation(1L, concertEvent.getId(), user);
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

	@Test
	@DisplayName("동시성 제어 적용, 10개 동시 예매 1개 성공")
	@Transactional
	public void testCreateReservations_Redis() {
		// Given: 10개의 예약을 동시에 진행
		int numberOfReservations = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfReservations);

		// 모든 스레드가 시작할 때까지 대기하는 CyclicBarrier 설정
		CyclicBarrier barrier = new CyclicBarrier(numberOfReservations);

		// 10개의 예약을 동시에 진행
		for (int i = 0; i < numberOfReservations; i++) {
			executorService.submit(() -> {
				try {
					// 모든 스레드가 이 지점에 도달할 때까지 대기
					barrier.await();

					int index = userIndex.getAndIncrement() % users.size();
					User user = users.get(index);

					// 예약 수행
					reservationService.createReservation(1L, concertEvent.getId(), user);
				} catch (Exception e) {
					e.printStackTrace();
				}

			});
		}

		// 스레드 작업이 완료될 때까지 기다리기
		executorService.shutdown();
		try {
			// awaitTermination에서 InterruptedException을 처리해야 합니다.
			if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
				executorService.shutdownNow();  // 작업이 아직 종료되지 않았다면 강제로 종료
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();  // 현재 스레드의 인터럽트 상태를 복원
			executorService.shutdownNow();  // 강제 종료
		}

		// 예약된 좌석 개수를 기존 방식으로 확인 (List로 변경된 메서드 사용)
		List<Reservation> reservations = reservationRepository.findBySeatNumAndEventId(1L, concertEvent.getId());
		long reservationCount = reservations.size();

		// Then: 성공한 예약의 수가 1개이어야 함
		assertEquals(1, reservationCount, "성공한 예약의 개수가 1이어야 합니다.");
	}
}
