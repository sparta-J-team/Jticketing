package com.project.jticketing.domain.concert.entity;

import com.project.jticketing.domain.common.entity.Timestamped;
import com.project.jticketing.domain.place.entity.Place;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "concerts")
public class Concert extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Date startDay;

    private Date endDay;

    private String startTime;

    private String endTime;

    private String description;

    private Long price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}
