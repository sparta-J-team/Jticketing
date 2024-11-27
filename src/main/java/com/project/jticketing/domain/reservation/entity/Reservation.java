package com.project.jticketing.domain.reservation.entity;

import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reservations")
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
    @JoinColumn(name = "events_id", nullable = false)
    private Event event;

    public Reservation(Long seatNum, LocalDateTime reservationDate, User user, Event event) {
        this.seatNum = seatNum;
        this.reservationDate = reservationDate;
        this.user = user;
        this.event = event;
    }
}
