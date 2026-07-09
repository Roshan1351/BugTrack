package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.Bug;
import com.bugtrack.bugtrack.entity.BugAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BugAttachmentRepository extends JpaRepository<BugAttachment, Integer> {
    List<BugAttachment> findByBug_BugId(Integer bugId);
}
