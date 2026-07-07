package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.Severity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeverityRepository extends JpaRepository<Severity, Integer> {
    Optional<Severity> findBySeverityName(String severityName);
}
