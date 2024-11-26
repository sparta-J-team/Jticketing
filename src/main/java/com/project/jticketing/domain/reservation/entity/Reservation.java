package com.project.jticketing.domain.reservation.entity;

import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations")
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seatNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Reservation(Long seatNum, User user, Event event) {
        this.seatNum = seatNum;
        this.user = user;
        this.event = event;
    }
}
