package com.project.jticketing.domain.reservation.controller;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.service.ReservationService;
import com.project.jticketing.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/event/Lettuce/{eventId}")
    public ResponseEntity<Boolean> reservationSeat(
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @PathVariable Long eventId,
            @RequestBody ReservationRequestDTO reservationRequestDTO ) {

        return new ResponseEntity<Boolean>(
                reservationService.reserveSeatWithRedisWithAop(authUser,eventId,reservationRequestDTO.getSeatNum()),
                HttpStatus.OK);
    }

    @PostMapping("/event/exclusiveLock/{eventId}")
    public ResponseEntity<Boolean> reservationSeat1(
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @PathVariable Long eventId,
            @RequestBody ReservationRequestDTO reservationRequestDTO ) {

        User user = authUser.getUser();

        return new ResponseEntity<Boolean>(
                reservationService.exclusiveLock(user,eventId,reservationRequestDTO.getSeatNum()),
                HttpStatus.OK);
    }

    @PostMapping("/event/redisson/{eventId}")
    public ResponseEntity<Boolean> reservationSeat2(
            @AuthenticationPrincipal UserDetailsImpl authUser,
            @PathVariable Long eventId,
            @RequestBody ReservationRequestDTO reservationRequestDTO ) {

        return new ResponseEntity<Boolean>(
                reservationService.reserveSeatWithRedisson(authUser,eventId,reservationRequestDTO.getSeatNum()),
                HttpStatus.OK);
    }

}







