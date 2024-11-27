package com.project.jticketing.Reservation;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.reservation.service.ReservationService;
import com.project.jticketing.domain.user.entity.User;
import com.project.jticketing.domain.user.enums.UserRole;
import com.project.jticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent;
    private List<UserDetailsImpl> testUserDetails;

    @BeforeEach
    public void setup() {
        // Create test event
        testEvent = eventRepository.save(Event.builder()
                .concertDate(LocalDate.now())
                .build());

        // Create test users
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
}
