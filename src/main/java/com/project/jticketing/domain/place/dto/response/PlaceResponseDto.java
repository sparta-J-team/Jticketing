package com.project.jticketing.domain.place.dto.response;

import com.project.jticketing.domain.place.entity.Place;

import lombok.Getter;

@Getter
public class PlaceResponseDto {
	private Long id;
	private String name;
	private Long seatCount;

	public PlaceResponseDto(Place place) {
		this.id = place.getId();
		this.name = place.getName();
		this.seatCount = place.getSeatCount();
	}
}
