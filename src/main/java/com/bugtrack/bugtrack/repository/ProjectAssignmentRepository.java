package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.ProjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ProjectAssignmentRepository extends JpaRepository<ProjectAssignment, Integer> {
    List<ProjectAssignment> findByProject_ProjectId(Integer projectId);

    List<ProjectAssignment> findByUser_userId(Integer userId);

    List<ProjectAssignment> findByProject_ProjectIdAndRoleInProject(Integer projectId, ProjectAssignment.ProjectRole role);

    Optional<ProjectAssignment> findByProject_ProjectIdAndUser_UserId(Integer projectId, Integer userId);
}
