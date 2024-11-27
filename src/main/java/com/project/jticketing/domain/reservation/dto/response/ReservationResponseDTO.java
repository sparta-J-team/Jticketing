package com.project.jticketing.domain.reservation.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResponseDTO {
	private Long reservationId;
	private Long seatNum;
	private LocalDate reservationDate;
	private Long concertId;
	private LocalDate concertDate;
}
