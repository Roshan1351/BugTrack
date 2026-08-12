package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.request.AssignMemberRequest;
import com.bugtrack.bugtrack.dto.request.CreateProjectRequest;
import com.bugtrack.bugtrack.dto.response.ProjectMemberResponse;
import com.bugtrack.bugtrack.dto.response.ProjectResponse;
import com.bugtrack.bugtrack.dto.response.UserResponse;
import com.bugtrack.bugtrack.entity.Project;
import com.bugtrack.bugtrack.entity.ProjectAssignment;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.ProjectAssignmentRepository;
import com.bugtrack.bugtrack.repository.ProjectRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectAssignmentRepository projectAssignmentRepository;

    @Autowired
    private UserRepository userRepository;


    //create project
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request){
        String email= SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User createdBy= userRepository.findByEmail(email).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        Project project=new Project();
        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setCreatedBy(createdBy);
        project.setStatus(Project.ProjectStatus.Active);
        Project saved= projectRepository.save(project);
        return (ProjectResponse) maptoResponse(saved);
    }

    public List<ProjectResponse> getAllProjects(){
        return projectRepository.findAllWithCreatedBy()
                .stream()
                .map(this::maptoResponse)
                .collect(Collectors.toList());
    }

    //fetch project by project id
    public ProjectResponse getProjectById(Integer projectId){
        Project project= projectRepository.findByIdWithCreatedBy(projectId).orElseThrow(()->new RuntimeException("Project not found: "+ projectId));
        return maptoResponse(project);
    }

    //update project status
    @Transactional
    public ProjectResponse updateProjectStatus(Integer projectId, String status){
        Project project= projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("project not found: "+ projectId));
        project.setStatus(Project.ProjectStatus.valueOf(status));
        return maptoResponse(projectRepository.save(project));
    }

    //team member assign
    @Transactional
    public ProjectMemberResponse assignMember(Integer projectId, AssignMemberRequest request){
        Project project= projectRepository.findById(projectId).orElseThrow(()->new RuntimeException("project not found: "+ projectId));
        User user= userRepository.findById(request.getUserId()).orElseThrow(()->new RuntimeException("User not found: "+ request.getUserId()));

        projectAssignmentRepository.findByProject_ProjectIdAndUser_UserId(projectId, request.getUserId()).ifPresent(a->{
            throw new RuntimeException("User already assigned to this project");
        });

        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User assignedBy= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        ProjectAssignment assignment = new ProjectAssignment();

        assignment.setProject(project);
        assignment.setUser(user);
        assignment.setRoleInProject(ProjectAssignment.ProjectRole.valueOf(request.getRoleInProject()));
        assignment.setAssignedBy(assignedBy);

        ProjectAssignment saved= projectAssignmentRepository.save(assignment);
        return new ProjectMemberResponse(
                saved.getAssignmentId(),
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                saved.getRoleInProject().name()
        );
    }

    public List<ProjectMemberResponse> getProjectMembers(Integer projectId){
        List<ProjectAssignment> list= projectAssignmentRepository.findByProject_ProjectId(projectId);
        List<ProjectMemberResponse> projectmember= list.stream().map(a->new ProjectMemberResponse(a.getAssignmentId(),a.getUser().getUserId(),a.getRoleInProject().name(),a.getUser().getEmail(), a.getUser().getFullName())).toList();

        return projectmember;
    }

    @Transactional
    public void removeMember(Integer assignmentId){
        projectAssignmentRepository.findById(assignmentId).orElseThrow(()->new RuntimeException("assignment not found: "+ assignmentId));

        projectAssignmentRepository.deleteById(assignmentId);

    }

    private ProjectResponse maptoResponse(Project project) {
        return new ProjectResponse(
                project.getProjectId(),
                project.getProjectName(),
                project.getDescription(),
                project.getStatus().name(),
                project.getCreatedBy().getFullName(),
                project.getCreatedAt()
        );
    }
}
