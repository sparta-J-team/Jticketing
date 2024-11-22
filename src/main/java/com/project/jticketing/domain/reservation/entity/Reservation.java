package com.project.jticketing.domain.reservation.entity;

import com.project.jticketing.domain.concert.entity.Concert;
import com.project.jticketing.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seatNum;

    private LocalDateTime reservationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;
}
