package com.project.jticketing.domain.reservation.service;

import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.entity.Reservation;
import com.project.jticketing.domain.reservation.repository.ReservationRepository;
import com.project.jticketing.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor  // final 이 붙은 필드만 생성자로 만들어줌.
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    public String reserve(User user, Long eventId, ReservationRequestDTO requestDTO) {
        Long seatNum = requestDTO.getSeatNum();

        if (reservationRepository.existsBySeatNum(seatNum)) {
            throw new IllegalArgumentException("해당 좌석은 예매가 완료 되었습니다.");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new IllegalArgumentException("해당 콘서트 정보가 존재하지 않습니다")
        );

        Reservation reservation = new Reservation(seatNum, user, event);
        Reservation savedReservation = reservationRepository.save(reservation);

        return "예매 성공";
    }

}
