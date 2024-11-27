package com.project.jticketing.domain.reservation.controller;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    // @PostMapping("/event/{eventId}")
    // public ResponseEntity<Boolean> reservationSeat(
    //         @AuthenticationPrincipal UserDetailsImpl authUser,
    //         @PathVariable Long eventId,
    //         @RequestBody ReservationRequestDTO reservationRequestDTO ) {
    //
    //     return new ResponseEntity<Boolean>(
    //             reservationService.reserveSeatWithRedisWithAop(authUser,eventId,reservationRequestDTO.getSeatNum()),
    //             HttpStatus.OK);
    // }

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Boolean> reservationSeat(
        @AuthenticationPrincipal UserDetailsImpl authUser,
        @PathVariable Long eventId,
        @RequestBody ReservationRequestDTO reservationRequestDTO ) {

        return new ResponseEntity<Boolean>(
            reservationService.reserveSeatWithJPALock(authUser,eventId,reservationRequestDTO.getSeatNum()),
            HttpStatus.OK);
    }
}
