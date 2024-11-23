package com.project.jticketing.domain.concert.repository;

import com.project.jticketing.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    @Query("SELECT c FROM Concert c WHERE c.place.id = :placeId AND c.startTime = :startTime")
    Optional<Concert> findByPlaceAndStartTime(@Param("placeId") Long placeId, @Param("startTime") String startTime);
}
