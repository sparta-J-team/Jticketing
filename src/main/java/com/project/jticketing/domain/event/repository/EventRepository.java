package com.project.jticketing.domain.event.repository;

import com.project.jticketing.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
