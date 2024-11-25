package com.project.jticketing.domain.concert.controller;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.concert.dto.request.ConcertRequestDto;
import com.project.jticketing.domain.concert.dto.response.ConcertDetailResponseDto;
import com.project.jticketing.domain.concert.dto.response.ConcertListResponseDto;
import com.project.jticketing.domain.concert.dto.response.ConcertResponseDto;
import com.project.jticketing.domain.concert.service.ConcertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/concert")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @PostMapping
    public ResponseEntity<ConcertResponseDto> registerConcert(
            @RequestBody @Valid ConcertRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ConcertResponseDto responseDto = concertService.registerConcert(requestDto, userDetails);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<ConcertListResponseDto> getAllConcerts(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ConcertListResponseDto responseDto = concertService.getAllConcerts(userDetails);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @GetMapping("/{concertId}")
    public ResponseEntity<ConcertDetailResponseDto> getConcertDetail(
            @PathVariable Long concertId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {


        ConcertDetailResponseDto concertDetail = concertService.getConcertDetail(concertId, userDetails);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(concertDetail);
    }

    @PutMapping("/api/concert/{concertId}")
    public ResponseEntity<ConcertResponseDto> updateConcert(@PathVariable Long concertId,
                                                            @Valid @RequestBody ConcertRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ConcertResponseDto responseDto = concertService.updateConcert(concertId, requestDto, userDetails);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }
}
