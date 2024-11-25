package com.project.jticketing.domain.concert.repository;

import com.project.jticketing.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    Optional<Concert> findByPlaceIdAndStartTime(Long placeId, String startTime);

    @Query("SELECT c FROM Concert c JOIN c.events e WHERE c.place.id = :placeId AND e.concertDate IN :eventDates")
    Optional<Concert> findByPlaceAndEventDateIn(@Param("placeId") Long placeId, @Param("eventDates") List<LocalDate> eventDates);
}
