package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.BugStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BugStatusRepository extends JpaRepository<BugStatus, Integer>  {
    Optional<BugStatus> findByStatusName(String statusName);
}
