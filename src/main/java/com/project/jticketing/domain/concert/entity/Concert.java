package com.project.jticketing.domain.concert.entity;

import com.project.jticketing.domain.common.entity.Timestamped;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.place.entity.Place;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concerts")
public class Concert extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String startTime;

    private String endTime;

    private String description;

    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    public void update(String title, String startTime, String endTime, Long price, String description, Place place, List<String> eventsDate) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.description = description;
        this.place = place;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Event> updatedEvents = eventsDate.stream()
                .map(eventDate -> Event.builder()
                        .concertDate(LocalDate.parse(eventDate, formatter))
                        .concert(this)
                        .build())
                .toList();

        this.events.clear();
        this.events.addAll(updatedEvents);
    }
}