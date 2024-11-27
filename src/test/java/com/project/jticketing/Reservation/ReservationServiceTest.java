package com.project.jticketing.Reservation;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.concert.entity.Concert;
import com.project.jticketing.domain.concert.repository.ConcertRepository;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.place.entity.Place;
import com.project.jticketing.domain.place.repository.PlaceRepository;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.reservation.service.ReservationService;
import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.enums.UserRole;
import com.project.jticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private PlaceRepository placeRepository;

    private Event testEvent;
    private List<UserDetailsImpl> testUserDetails;

    @BeforeEach
    public void setup() {

        reservationRepository.deleteAll(); // Clear reservations first
        eventRepository.deleteAll(); // Clear events
        concertRepository.deleteAll(); // Clear concerts
        placeRepository.deleteAll(); // Clear places
        userRepository.deleteAll(); // Clear users

        Place testPlace = placeRepository.save(
                Place.builder()
                        .name("ABCD")
                        .seatCount(500L)
                        .build());


        Concert testConcert = concertRepository.save(Concert.builder()
                .title("testConcert")
                .startTime("18:00")
                .endTime("21:00")
                .description("설명")
                .price(50000L)
                .place(testPlace)
                .build());

        testEvent = eventRepository.save(Event.builder()
                .concert(testConcert)
                .concertDate(LocalDate.now())
                .build());

        testUserDetails = IntStream.range(0, 10)
                .mapToObj(i -> {
                    User user = userRepository.save(new User(
                            "user" + i + "@test.com",
                            "password",
                            "User " + i,
                            "Address",
                            "010-1234-567" + i,
                            UserRole.USER
                    ));
                    return new UserDetailsImpl(user);
                })
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("Redis 없음")
    public void testConcurrentSeatBooking() throws InterruptedException {
        int threadCount = 10;
        Long seatNum = 1L;
        Long eventId = testEvent.getId();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        List<Future<Boolean>> results = new ArrayList<>();

        for (UserDetailsImpl userDetails : testUserDetails) {
            results.add(executorService.submit(() -> {
                barrier.await();
                try {
                    reservationService.bookSeat(userDetails, eventId, seatNum);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }));
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        long successCount = results.stream()
                .filter(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        assertEquals(1, successCount, "좌석 예약은 한 개만 성공해야 합니다.");
    }

    @Test
    @DisplayName("bookSeat 동시성 test")
    public void testConcurrentSeatBookingRedis() throws InterruptedException {
        int threadCount = 10;
        Long seatNum = 1L;
        Long eventId = testEvent.getId();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        List<Future<Boolean>> results = new ArrayList<>();

        for (UserDetailsImpl userDetails : testUserDetails) {
            results.add(executorService.submit(() -> {
                barrier.await();
                try {
                    reservationService.bookSeatRedis(userDetails, eventId, seatNum);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }));
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        long successCount = results.stream()
                .filter(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        assertEquals(1, successCount, "[동시성 제어]좌석 예약은 한 개만 성공해야 합니다.");
    }

    @Test
    @DisplayName("예매 단건 하기")
    public void testSingleBookingRedis() {
        Long seatNum = 1L;
        Long eventId = testEvent.getId();
        UserDetailsImpl userDetails = testUserDetails.get(0);

        assertDoesNotThrow(() -> reservationService.bookSeatRedis(userDetails, eventId, seatNum));

        assertTrue(reservationRepository.findByEventAndSeatNum(testEvent, seatNum).isPresent(),
                "예약이 성공적으로 저장되어야 합니다.");
    }
}
