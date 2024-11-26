package com.project.jticketing.domain.event.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.project.jticketing.domain.concert.entity.Concert;
import com.project.jticketing.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    void deleteAllByConcert(Concert concert);

	Optional<Event> findByConcertIdAndConcertDate(Long concertId, LocalDate concertDate);
}
