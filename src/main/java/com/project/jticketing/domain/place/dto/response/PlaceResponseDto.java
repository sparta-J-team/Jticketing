package com.project.jticketing.domain.place.dto.response;

import com.project.jticketing.domain.place.entity.Place;

public class PlaceResponseDto {
	private Long id;
	private String name;
	private Long seatCount;

	public PlaceResponseDto(Place place) {
		this.id = id;
		this.name = name;
		this.seatCount = seatCount;
	}
}
