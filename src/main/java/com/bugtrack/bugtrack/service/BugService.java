package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.request.CreateBugRequest;
import com.bugtrack.bugtrack.dto.request.UpdateBugStatusRequest;
import com.bugtrack.bugtrack.dto.response.BugResponse;
import com.bugtrack.bugtrack.entity.*;
import com.bugtrack.bugtrack.repository.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BugService {

    private final BugRepository bugRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final BugStatusRepository bugStatusRepository;
    private final PriorityRepository priorityRepository;
    private final SeverityRepository severityRepository;
    private final BugStatusHistoryRepository historyRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public BugResponse createBug(CreateBugRequest request){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User tester= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));

        Project project= projectRepository.findById(request.getProjectId()).orElseThrow(()->new RuntimeException("Project not found"));

        BugStatus openStatus= bugStatusRepository.findByStatusName("Open").orElseThrow(()->new RuntimeException("Status not found"));

        Priority priority= priorityRepository.findByPriorityName(request.getPriorityName()).orElseThrow(()-> new RuntimeException("Priority not found: "+ request.getPriorityName()));

        Severity severity= severityRepository.findBySeverityName(request.getSeverityName()).orElseThrow(()->new RuntimeException("Severity not found: "+ request.getSeverityName()));

        Bug bug= new Bug();
        bug.setProject(project);
        bug.setTitle(request.getTitle());
        bug.setDescription(request.getDescription());
        bug.setStepsToReproduce(request.getStepsToReproduce());
        bug.setRaisedBy(tester);
        bug.setStatus(openStatus);
        bug.setPriority(priority);
        bug.setSeverity(severity);
        bug.setDueDate(calculateDueDate(request.getPriorityName()));

        Bug saved= bugRepository.save(bug);
        logStatusHistory(saved, null, openStatus, tester, "Bug raised");
        return mapToResponse(saved);
    }

    public BugResponse mapToResponse(Bug bug) {
        return BugResponse.builder()
                .bugId(bug.getBugId())
                .title(bug.getTitle())
                .description(bug.getDescription())
                .stepsToReproduce(bug.getStepsToReproduce())
                .projectName(bug.getProject().getProjectName())
                .raisedBy(bug.getRaisedBy().getFullName())
                .assignedTo(bug.getAssignedTo() != null
                        ? bug.getAssignedTo().getFullName() : "Unassigned")
                .status(bug.getStatus().getStatusName())
                .priority(bug.getPriority().getPriorityName())
                .severity(bug.getSeverity().getSeverityName())
                .dueDate(bug.getDueDate())
                .createdAt(bug.getCreatedAt())
                .resolvedAt(bug.getResolvedAt())
                .build();
    }

    private void logStatusHistory(Bug bug, BugStatus oldStatus, BugStatus newStatus, User changedBy, String remarks) {
        BugStatusHistory history= new BugStatusHistory();
        history.setBug(bug);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setRemarks(remarks);
        historyRepository.save(history);

    }

    private LocalDateTime calculateDueDate(@NotBlank(message = "priority is required") String priorityName) {
        return switch (priorityName){
            case "Critical"-> LocalDateTime.now().plusHours(24);
            case "High"-> LocalDateTime.now().plusDays(3);
            case "Medium"-> LocalDateTime.now().plusDays(7);
            case "Low"-> LocalDateTime.now().plusDays(15);
            default ->  LocalDateTime.now().plusDays(7);
        };
    }

    @Transactional
    public BugResponse updateBugStatus(Integer BugId, UpdateBugStatusRequest request){
        Bug bug= bugRepository.findById(BugId).orElseThrow(()-> new RuntimeException("Bug not found: "+ BugId));

        BugStatus newStatus= bugStatusRepository.findByStatusName(request.getStatusName()).orElseThrow(()->
                new RuntimeException("Status not found: "+ request.getStatusName()));

        BugStatus oldStatus = bug.getStatus();

        if(request.getStatusName().equals("Resolved")) {
            bug.setResolvedAt(LocalDateTime.now());

            sendNotification(bug.getRaisedBy(), bug, "Bug resolved, please re-test: " + bug.getTitle());
        }
        if(request.getStatusName().equals("Closed")){
            bug.setClosedAt(LocalDateTime.now());
        }

        if(request.getStatusName().equals("Reopened")){
            sendNotification(bug.getAssignedTo(), bug, "Bug reopened, need fix: "+ bug.getTitle());
        }
        bug.setStatus(newStatus);
        Bug updated = bugRepository.save(bug);

        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User changedBy= userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));

        logStatusHistory(updated, oldStatus, newStatus, changedBy, request.getRemark());
        return mapToResponse(updated);
    }

    private void sendNotification(User user, Bug bug, String message) {
        if(user==null){
            return;
        }
        Notification notification= new Notification();
        notification.setUser(user);
        notification.setBug(bug);
        notification.setMessage(message);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }
}
