package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.BugComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BugCommentRepository extends JpaRepository<BugComment, Integer> {
    List<BugComment> findByBug_BugIdOrderByCreatedAtAsc(Integer bugId);
}
