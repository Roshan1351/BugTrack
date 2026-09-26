package com.bugtrack.bugtrack.controller;

import com.bugtrack.bugtrack.dto.request.AssignMemberRequest;
import com.bugtrack.bugtrack.dto.request.CreateProjectRequest;
import com.bugtrack.bugtrack.dto.response.ProjectMemberResponse;
import com.bugtrack.bugtrack.dto.response.ProjectResponse;
import com.bugtrack.bugtrack.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {
    public final ProjectService projectService;

    //create project by only admin, and project manager.
    @PostMapping
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(request));
    }

    //get all project list with detail
    @GetMapping
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager') or hasRole('Tester') or hasRole('Developer')")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    //get project by project id.
    @GetMapping("/{projectId}")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')" +
            " or hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Integer projectId) {
        return ResponseEntity.ok(
                projectService.getProjectById(projectId)
        );
    }

    //update status of project
    @PatchMapping("/{projectId}/status")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<ProjectResponse> updateStatus(
            @PathVariable Integer projectId,
            @RequestParam String status) {
        return ResponseEntity.ok(
                projectService.updateProjectStatus(projectId, status)
        );
    }

    //assign member to project
    @PostMapping("/{projectId}/members")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<ProjectMemberResponse> assignMember(
            @PathVariable Integer projectId,
            @Valid @RequestBody AssignMemberRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.assignMember(projectId, request));
    }

    //get members which is currently work in project
    @GetMapping("/{projectId}/members")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')" +
            " or hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(
            @PathVariable Integer projectId) {
        return ResponseEntity.ok(
                projectService.getProjectMembers(projectId)
        );
    }

    //remove any member from project.
    @DeleteMapping("/members/{assignmentId}")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<String> removeMember(
            @PathVariable Integer assignmentId) {
        projectService.removeMember(assignmentId);
        return ResponseEntity.ok("Member removed successfully!");
    }
}
