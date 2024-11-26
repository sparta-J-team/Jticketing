package com.project.jticketing.domain.reservation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationRequestDTO {
	@NotNull
	@Min(1)
	private Long seatNum;
}
