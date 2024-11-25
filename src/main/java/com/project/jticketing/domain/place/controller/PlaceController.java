package com.project.jticketing.domain.place.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.jticketing.domain.place.dto.request.PlaceRequestDto;
import com.project.jticketing.domain.place.dto.response.PlaceResponseDto;
import com.project.jticketing.domain.place.service.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class PlaceController {
	private final PlaceService placeService;

	@PostMapping("")
	public ResponseEntity<PlaceResponseDto> createPlace(
		@RequestBody PlaceRequestDto placeRequestDto
		//추후 권한 추가
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(placeService.createPlace(placeRequestDto));
	}

	@GetMapping("")
	public ResponseEntity<Page<PlaceResponseDto>> getPlaces(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.status(HttpStatus.OK).body(placeService.getPlaces(page, size));
	}

	@GetMapping("/{placeId}")
	public ResponseEntity<PlaceResponseDto> getPlace(
		@PathVariable Long placeId
	) {
		return ResponseEntity.status(HttpStatus.OK).body(placeService.getPlace(placeId));
	}

	@PatchMapping("/{placeId}")
	public ResponseEntity<PlaceResponseDto> updatePlace(
		@PathVariable Long placeId,
		@RequestBody PlaceRequestDto placeRequestDto
		//추후 권한 추가
	) {
		return ResponseEntity.status(HttpStatus.OK).body(placeService.updatePlace(placeId, placeRequestDto));
	}

	@DeleteMapping("/{placeId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePlace(
		@PathVariable Long placeId
	) {
		placeService.deletePlace(placeId);
	}
}
