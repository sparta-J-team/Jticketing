package com.project.jticketing.domain.concert.repository;

import com.project.jticketing.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    @Query("SELECT c FROM Concert c LEFT JOIN FETCH c.events e LEFT JOIN FETCH c.place")
    List<Concert> findAllWithEvents();

    @Query("SELECT c FROM Concert c LEFT JOIN FETCH c.events e LEFT JOIN FETCH c.place WHERE c.id = :concertId")
    Optional<Concert> findByIdWithEventsAndPlace(@Param("concertId") Long concertId);

    @Query("SELECT c FROM Concert c LEFT JOIN FETCH c.events e WHERE c.place.id = :placeId AND c.startTime = :startTime AND e.concertDate IN :eventDates")
    Optional<Concert> findByPlaceAndStartTimeAndEventDates(
            @Param("placeId") Long placeId,
            @Param("startTime") String startTime,
            @Param("eventDates") List<LocalDate> eventDates);

}
