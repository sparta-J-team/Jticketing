package com.project.jticketing.domain.place.entity;

import com.project.jticketing.domain.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "places")
public class Place extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long seatCount;

    public Place(String name, Long seatCount) {
        this.name = name;
        this.seatCount = seatCount;
    }

    public static Place createOf(String name, Long seatCount) {
        return new Place(name, seatCount);
    }

    public void updateOf(String name, Long seatCount) {
        this.name = name;
        this.seatCount = seatCount;
    }
}
