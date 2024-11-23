package com.project.jticketing.domain.concert.controller;

import com.project.jticketing.domain.concert.dto.request.ConcertRegisterRequestDto;
import com.project.jticketing.domain.concert.dto.response.ConcertRegisterResponseDto;
import com.project.jticketing.domain.concert.service.ConcertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/concert")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    //Admin 인증 추가 예정
    @PostMapping
    public ResponseEntity<ConcertRegisterResponseDto> registerConcert(
            @RequestBody @Valid ConcertRegisterRequestDto requestDto,
            @RequestHeader("Authorization") String authorization) {

        ConcertRegisterResponseDto responseDto = concertService.registerConcert(requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }
}
