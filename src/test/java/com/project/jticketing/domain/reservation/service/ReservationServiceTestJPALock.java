package com.project.jticketing.domain.reservation.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.concert.entity.Concert;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.place.entity.Place;
import com.project.jticketing.domain.place.repository.PlaceRepository;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.enums.UserRole;
import com.project.jticketing.domain.user.repository.UserRepository;

@SpringBootTest
class ReservationServiceTestJPALock {

	@Autowired
	private ReservationService reservationService; // 예약 서비스
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private UserRepository userRepository;

	private Long eventId; // 테스트용 이벤트 ID

	private User testUser;
	private Event testEvent;
	private Place testPlace;
	private UserDetailsImpl testUserDetails;

	@Test
	@Transactional
	void reserveSeatWithJPALock() throws InterruptedException, BrokenBarrierException {
		{
			Long seatNum = 2L; // 테스트할 좌석 번호
			int threadCount = 5; // 동시 실행할 스레드 수

			// CyclicBarrier로 동시 시작점 조율
			CyclicBarrier barrier = new CyclicBarrier(threadCount);

			// 스레드 풀 설정
			ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

			// 동시 예약 테스트 실행
			for (int i = 1; i <= threadCount; i++) {
				final int userId = i;
				executorService.execute(() -> {
					try {
						barrier.await(); // 모든 스레드가 준비될 때까지 대기
						User user = userRepository.findById((long)userId).orElseThrow(
							() -> new RuntimeException("유저를 찾을 수 없습니다."));
						testUserDetails = new UserDetailsImpl(user);
						reservationService.reserveSeatWithJPALock(testUserDetails, 1L, seatNum);

					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}

			executorService.shutdown();
			executorService.awaitTermination(1, TimeUnit.MINUTES);

			// 예약된 좌석 개수를 기존 방식으로 확인 (List로 변경된 메서드 사용)
			reservationRepository.findByEventIdAndSeatNumWithLock(1L, seatNum);
			// 예약된 좌석이 존재하면 성공한 예약 수는 1개여야 함
			long reservationCount = reservationRepository.count();  // 예약 개수 확인
			assertEquals(1, reservationCount);  // 기대한 개수와 비교
		}
	}
}