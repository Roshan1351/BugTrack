package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriorityRepository extends JpaRepository<Priority, Integer> {
    Optional<Priority> findByPriorityName(String priorityName);
}
