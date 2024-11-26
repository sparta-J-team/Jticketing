package com.project.jticketing.domain.reservation.controller;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.reservation.dto.request.ReservationRequestDTO;
import com.project.jticketing.domain.reservation.service.ReservationService;
import com.project.jticketing.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/api/concert/{eventId}")
    public String reserve(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long eventId,
            @RequestBody ReservationRequestDTO requestDTO) {
        User user = userDetails.getUser();
        return reservationService.reserve(user, eventId, requestDTO);
    }

}
