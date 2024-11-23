package com.project.jticketing.domain.concert.entity;

import com.project.jticketing.domain.common.entity.Timestamped;
import com.project.jticketing.domain.place.entity.Place;
import jakarta.persistence.*;
import lombok.*;

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

    public Concert (String title, String startTime, String endTime, Long price, Place place) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.place = place;
    }
}
