package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.BugStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BugStatusHistoryRepository extends JpaRepository<BugStatusHistory, Integer> {
    List<BugStatusHistory> findByBug_BugIdOrderByChangedAtAsc(Integer bugId);
}
