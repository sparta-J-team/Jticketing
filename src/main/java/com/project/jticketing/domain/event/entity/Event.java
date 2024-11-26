package com.project.jticketing.domain.event.entity;

import com.project.jticketing.domain.concert.entity.Concert;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate concertDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    public Event(LocalDate concertDate, Concert concert) {
        this.concertDate = concertDate;
        this.concert = concert;
    }
}
