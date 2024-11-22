package com.project.jticketing.domain.concert.repository;

import com.project.jticketing.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
}
