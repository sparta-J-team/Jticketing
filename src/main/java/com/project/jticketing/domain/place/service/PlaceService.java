package com.project.jticketing.domain.place.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.jticketing.domain.place.dto.request.PlaceRequestDto;
import com.project.jticketing.domain.place.dto.response.PlaceResponseDto;
import com.project.jticketing.domain.place.entity.Place;
import com.project.jticketing.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;

	public PlaceResponseDto createPlace(PlaceRequestDto placeRequestDto) {
		Place place = Place.createOf(placeRequestDto.getName(), placeRequestDto.getSeatCount());

		//권한 관련 예외처리 코드

		return new PlaceResponseDto(placeRepository.save(place));
	}

	public Page<PlaceResponseDto> getPlaces(int page, int size) {
		Pageable pageable = PageRequest.of(page-1, size);
		Page<Place> places = placeRepository.findAll(pageable);

		return places.map(PlaceResponseDto::new);
	}

	public PlaceResponseDto getPlace(Long placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 콘서트 장소가 존재하지 않습니다"));

		return new PlaceResponseDto(place);
	}

	public PlaceResponseDto updatePlace(Long placeId, PlaceRequestDto placeRequestDto) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 콘서트 장소가 존재하지 않습니다"));

		//권한 관련 예외처리 코드

		place.updateOf(placeRequestDto.getName(), placeRequestDto.getSeatCount());

		return new PlaceResponseDto(placeRepository.save(placeRepository.save(place)));

	}

	public void deletePlace(Long placeId) {
		Place place = placeRepository.findById(placeId)
			.orElseThrow(() -> new IllegalArgumentException("해당 콘서트 장소가 존재하지 않습니다"));

		//권한 관련 예외처리 코드

		placeRepository.delete(place);
	}
}
