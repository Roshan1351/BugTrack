package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("SELECT p FROM Project p " +
            "JOIN FETCH p.createdBy")
    List<Project> findAllWithCreatedBy();

    @Query("SELECT p FROM Project p " +
            "JOIN FETCH p.createdBy " +
            "WHERE p.projectId = :projectId")
    Optional<Project> findByIdWithCreatedBy(Integer projectId);

    List<Project> findByCreatedBy_UserId(Integer userId);

    List<Project> findByStatus(Project.ProjectStatus status);
}
