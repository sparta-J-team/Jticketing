package com.project.jticketing.domain.reservation.entity;

import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@NoArgsConstructor
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
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Reservation(Long seatNum, User user, Event event, LocalDateTime date) {
        this.seatNum = seatNum;
        this.user = user;
        this.event = event;
        this.reservationDate = date;
    }
}
