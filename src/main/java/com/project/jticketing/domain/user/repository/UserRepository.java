package com.project.jticketing.domain.user.repository;

import com.project.jticketing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
