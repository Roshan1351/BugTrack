package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    List<Project> findByCreatedBy_UserId(Integer userId);

    List<Project> findByStatus(Project.ProjectStatus status);
}
