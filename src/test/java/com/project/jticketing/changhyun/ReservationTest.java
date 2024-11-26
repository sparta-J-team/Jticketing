package com.project.jticketing.changhyun;
import com.project.jticketing.config.redis.RedisLock;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private RedisLock redisLock;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("같은 자리-seatNum-에 동시에 10개의 요청이 들어오는 경우")
    void concurrencyTest() throws Exception {
        // GIVEN
        User user = new User();
        user.setId(1L);
        Long eventId = 1L;
        Long seatNum = 1L;
        ReservationRequestDTO requestDTO = new ReservationRequestDTO();
        requestDTO.setSeatNum(seatNum);
        int numberOfThreads = 10;

        Event event = Event.builder()
                .id(eventId)
                .build();

        // 락 획득 모킹
        when(redisLock.acquireLock(anyString(), anyLong()))
                .thenReturn(UUID.randomUUID().toString()) // 첫 번째 요청만 성공
                .thenReturn(null);  // 나머지 요청은 실패

        // 락 해제 모킹 (모든 요청에 대해 true 반환)
        when(redisLock.releaseLock(anyString(), anyString())).thenReturn(true);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(reservationRepository.existsBySeatNum(seatNum)).thenReturn(false);

        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        List<Future<String>> futures = new ArrayList<>();

        // WHEN
        for (int i = 0; i < numberOfThreads; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    barrier.await(); // 모든 쓰레드가 준비될 때까지 대기
                    return reservationService.reserve(user, eventId, requestDTO);
                } catch (Exception e) {
                    return e.getMessage();
                }
            }));
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // THEN
        long successCount = futures.stream()
                .filter(future -> {
                    try {
                        return "예매 성공".equals(future.get());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        // 1개의 예매만 성공해야 함, 1번만 save 호출 검증
        assertThat(successCount).isEqualTo(1);
        verify(reservationRepository, times(1)).save(any(Reservation.class));

        // 예외 발생 횟수는 9번이어야 함
        long failureCount = futures.stream()
                .filter(future -> {
                    try {
                        return "현재 다른 사용자가 해당 좌석을 예약 중입니다. 다시 시도하세요.".equals(future.get());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();
        assertThat(failureCount).isEqualTo(9);
    }
}
