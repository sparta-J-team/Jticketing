package com.project.jticketing.domain.place.repository;

import com.project.jticketing.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
