package com.project.jticketing.changhyun;

import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.reservation.service.ReservationService;
import com.project.jticketing.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private ReservationService reservationService;


    @Test
    @DisplayName("같은 자리-seatNum-에 동시에 10개의 요청이 들어오는 경우")
    void concurrencyTest() throws Exception {
        // GIVEN
        User user = new User();
        user.setId(1L);
        Long eventId = 1L;
        ReservationRequestDTO requestDTO = new ReservationRequestDTO();
        requestDTO.setSeatNum(1L);
        int numberOfThreads = 10;

        Event event = Event.builder()
                .id(eventId)
                .build();

        Reservation reservation = new Reservation(requestDTO.getSeatNum(), user, event);

        when(reservationRepository.existsBySeatNum(requestDTO.getSeatNum())).thenReturn(true);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads); // 동기화 도구
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // WHEN
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    barrier.await(); // 모든 쓰레드가 준비될 때까지 대기

                    // 동시성 테스트: 동일한 좌석에 대해 여러 요청을 보냄
                    reservationService.reserve(user, eventId, requestDTO);
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // THEN - 같은 좌석에는 1 번만 예매가 되어야함
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}
