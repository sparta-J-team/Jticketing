package com.project.jticketing.domain.reservation.service;

import com.project.jticketing.config.redis.LockService;
import com.project.jticketing.config.redis.RedisLockRepository;
import com.project.jticketing.config.security.UserDetailsImpl;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceMockTest {

    @Mock
    private LockService lockService;

    @Mock
    private ReservationRepository reservationRepository;  // Mock 객체

    @Mock
    private EventRepository eventRepository;  // Mock 객체

    @Mock
    private RedisLockRepository redisLockRepository;

    @Mock
    private UserRepository userRepository;  // Mock 객체

    @InjectMocks
    private ReservationService reservationService;  // ReservationService 객체에 Mock 객체가 주입됨

    private User testUser;
    private Event testEvent;
    private List<User> userList;

    private final int NUMBER_OF_EXCURSIONS = 100;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = new User("testuser6@example.com", "!Test1234", "tester", "address", "010-0000-0000", UserRole.USER);
        userList = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_EXCURSIONS; i++) {
            userList.add(
                    new User("teruser" + i + "@example.com",
                            "!Test1234",
                            "tester",
                            "address",
                            "010-0000-0000",
                            UserRole.USER)
            );
        }


        testEvent = Event.builder().id(1L).build();

        when(lockService.tryLock(anyString()))
                .thenReturn(true)
                .thenReturn(false);


        when(eventRepository.findById(any())).thenReturn(Optional.of(testEvent));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation(1L, LocalDateTime.now(), testUser, testEvent));
    }

    @Test
    @DisplayName("예약 단건 요청")
    void reserveSeat() {
        // Given
        Long seatNum = 1L;

        // UserDetailsImpl을 생성하여 인증된 사용자로 테스트
        UserDetailsImpl authUser = new UserDetailsImpl(testUser);

        // When
        boolean result = reservationService.reserveSeatWithRedis(authUser, testEvent.getId(), seatNum);

        // Then
        assertThat(result).isTrue();  // 예약이 성공했는지 확인
        verify(reservationRepository, times(1)).save(any(Reservation.class));  // save 메서드가 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("예매 동시성 테스트(Redis미사용)")
    void reserveSeat_withoutRedis_concurrentTest() throws InterruptedException {
        // Given
        final Long seatNum = 4L;

        // 스레드 풀과 CyclicBarrier 설정
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_EXCURSIONS);
        CyclicBarrier barrier = new CyclicBarrier(NUMBER_OF_EXCURSIONS);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < NUMBER_OF_EXCURSIONS; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    barrier.await();  // 모든 스레드가 이 지점에 도달할 때까지 대기

                    UserDetailsImpl authUser = new UserDetailsImpl(userList.get(index));
                    boolean result = reservationService.reserveSeatWithoutRedis(authUser, testEvent.getId(), seatNum);  // 예매 시도

                    System.out.println("Thread result: " + result);
                    if(result){
                        successCount.incrementAndGet();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);


        assertThat(successCount.get()).isEqualTo(1);  // 하나의 예약만 성공해야 함
    }


    @Test
    @DisplayName("예매 동시성 테스트(Redis사용)")
    void reserveSeat_concurrentTest() throws InterruptedException {
        // Given
        final Long seatNum = 4L;

        // 스레드 풀과 CyclicBarrier 설정
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_EXCURSIONS);
        CyclicBarrier barrier = new CyclicBarrier(NUMBER_OF_EXCURSIONS);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < NUMBER_OF_EXCURSIONS; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    barrier.await();  // 모든 스레드가 이 지점에 도달할 때까지 대기

                    UserDetailsImpl authUser = new UserDetailsImpl(userList.get(index));
                    boolean result = reservationService.reserveSeatWithRedis(authUser, testEvent.getId(), seatNum);  // 예매 시도

                    System.out.println("Thread result: " + result);
                    if(result){
                        successCount.incrementAndGet();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);


        assertThat(successCount.get()).isEqualTo(1);  // 하나의 예약만 성공해야 함
    }
}