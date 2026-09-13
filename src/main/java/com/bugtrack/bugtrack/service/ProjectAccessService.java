package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.entity.Bug;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.ProjectAssignmentRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectAccessService {
    private final UserRepository userRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isPrivileged(User user) {
        String role = user.getRole().getRoleName();
        return "Admin".equals(role) || "Project Manager".equals(role);
    }

    public boolean isAssignedToProject(Integer userId, Integer projectId) {
        return projectAssignmentRepository
                .findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .isPresent();
    }

    public void requireProjectAccess(Integer projectId) {
        User user = currentUser();
        if (isPrivileged(user)) {
            return;
        }
        if (!isAssignedToProject(user.getUserId(), projectId)) {
            throw new RuntimeException("You are not allocated to this project");
        }
    }

    public void requireBugAccess(Bug bug) {
        if (bug.getProject() == null) {
            throw new RuntimeException("Project not found for this bug");
        }
        requireProjectAccess(bug.getProject().getProjectId());
    }
}
