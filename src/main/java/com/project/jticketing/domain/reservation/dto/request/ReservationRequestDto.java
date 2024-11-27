package com.project.jticketing.domain.reservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {
    private Long eventId;
    private Long seatNum;
}
