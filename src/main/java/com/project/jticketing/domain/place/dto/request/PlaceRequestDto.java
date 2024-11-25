package com.project.jticketing.domain.place.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PlaceRequestDto {
	@NotNull
	private String name;

	@NotNull
	@Min(1)
	private Long seatCount;
}
