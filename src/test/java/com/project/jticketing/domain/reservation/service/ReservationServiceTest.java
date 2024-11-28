package com.project.jticketing.domain.reservation.service;


import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.concert.entity.Concert;
import com.project.jticketing.domain.concert.repository.ConcertRepository;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.place.entity.Place;
import com.project.jticketing.domain.place.repository.PlaceRepository;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.enums.UserRole;
import com.project.jticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationServiceTest {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Event testEvent;
    private Place testPlace;

    @BeforeEach
    void setUp() {
        String uniqueEmail = UUID.randomUUID() + "testuser@example.com";
        testUser = userRepository.save(new User(uniqueEmail, "!Test1234", "tester", "address", "010-0000-0000", UserRole.USER));

        testPlace = new Place("aaa", 100L);
        testPlace = placeRepository.save(testPlace);

        // 테스트 콘서트 생성
        Concert concert = Concert.builder()
                .title("testConcert")
                .startTime("18:00")
                .endTime("21:00")
                .description("설명")
                .price(50000L)
                .place(testPlace)
                .build();
        concert = concertRepository.save(concert);

        LocalDate concertDate = LocalDate.of(2024, 12, 25);

        testEvent = new Event(concertDate, concert);
        testEvent = eventRepository.save(testEvent);
    }

    @Test
    @DisplayName("Lock 구현  X -> 테스트 실패")
    void reserveSeatWithoutRedis_concurrentTest() throws InterruptedException {
        final int numberOfThreads = 100;
        final Long seatNum = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 모든 스레드가 시작할 때까지 대기하는 CyclicBarrier 설정
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 이 지점에 도달할 때까지 대기
                    barrier.await();

                    UserDetailsImpl authUser = new UserDetailsImpl(testUser);
                    boolean result = reservationService.reserveSeatWithoutRedis(authUser, testEvent.getId(), seatNum);
                    System.out.println("Thread result: " + result);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 스레드 작업이 완료될 때까지 기다리기
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);

        long reservationCount = reservationRepository.countByEventIdAndSeatNum(testEvent.getId(), seatNum);
        assertThat(reservationCount).isEqualTo(1); // 하나의 예약만 성공해야 함
    }

    @Test
    @DisplayName("Lettuce 테스트")
    void reserveSeatWithRedis_concurrentTest() throws InterruptedException {
        final int numberOfThreads = 10;
        final Long seatNum = 4L;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 모든 스레드가 시작할 때까지 대기하는 CyclicBarrier 설정
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 이 지점에 도달할 때까지 대기
                    barrier.await();

                    UserDetailsImpl authUser = new UserDetailsImpl(testUser);

                    boolean result = reservationService.reserveSeatWithRedis(authUser, testEvent.getId(), seatNum);
                    System.out.println("Thread result: " + result);
                    if (result) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 스레드 작업이 완료될 때까지 기다리기
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("seatNum : " + seatNum);

        assertThat(successCount.get()).isEqualTo(1);  // 하나의 예약만 성공해야 함
    }

    @Test
    @DisplayName("Redisson 테스트")
    void reserveSeatWithRedisson_concurrentTest() throws InterruptedException {
        final int numberOfThreads = 10;
        final Long seatNum = 4L;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 모든 스레드가 시작할 때까지 대기하는 CyclicBarrier 설정
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 이 지점에 도달할 때까지 대기
                    barrier.await();

                    UserDetailsImpl authUser = new UserDetailsImpl(testUser);

                    boolean result = reservationService.reserveSeatWithRedisson(authUser, testEvent.getId(), seatNum);
                    System.out.println("Thread result: " + result);
                    if (result) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 스레드 작업이 완료될 때까지 기다리기
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("seatNum : " + seatNum);

        assertThat(successCount.get()).isEqualTo(1);  // 하나의 예약만 성공해야 함
    }

    @Test
    @DisplayName("배타적 락 테스트")
    void testExclusiveLock() throws InterruptedException {
        final int numberOfThreads = 1;
        final Long seatNum = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 모든 스레드가 시작할 때까지 대기하는 CyclicBarrier 설정
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 모든 스레드가 이 지점에 도달할 때까지 대기
                    barrier.await();

                    boolean result = reservationService.exclusiveLock(testUser, testEvent.getId(), seatNum);
                    System.out.println("Thread result: " + result);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // 스레드 작업이 완료될 때까지 기다리기
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        long reservationCount = reservationRepository.countByEventIdAndSeatNum(testEvent.getId(), seatNum);
        assertThat(reservationCount).isEqualTo(1); // 하나의 예약만 성공해야 함

        // 저장된 Reservation 검증
        Optional<Reservation> savedReservation = reservationRepository.findByEventIdAndSeatNum(testEvent.getId(), seatNum);

        assertThat(savedReservation).isPresent(); // 예약이 존재하는지 확인
    }

    @Test
    @DisplayName("예매 단건 요청")
    void reserveSeatWithRedis() throws InterruptedException {

        UserDetailsImpl authUser = new UserDetailsImpl(testUser);
        boolean result = reservationService.reserveSeatWithRedis(authUser, testEvent.getId(), 1L);

        assertThat(result).isTrue(); // 예약이 성공했는지 확인

        // 데이터베이스에서 방금 저장한 예약 확인
        Optional<Reservation> savedReservation = reservationRepository.findByEventIdAndSeatNum(testEvent.getId(), 1L);

        long reservationCount = reservationRepository.countByEventIdAndSeatNum(testEvent.getId(), 1L);
        assertThat(reservationCount).isEqualTo(1); // 하나의 예약만 성공해야 함

        assertThat(savedReservation).isPresent();
        assertThat(savedReservation.get().getSeatNum()).isEqualTo(1L);
    }
}