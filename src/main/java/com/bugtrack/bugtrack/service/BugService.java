package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.request.CreateBugRequest;
import com.bugtrack.bugtrack.dto.request.UpdateBugStatusRequest;
import com.bugtrack.bugtrack.dto.response.BugResponse;
import com.bugtrack.bugtrack.dto.response.DuplicateResult;
import com.bugtrack.bugtrack.entity.*;
import com.bugtrack.bugtrack.repository.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private final DuplicateDetectionService duplicateDetectionService;
    private final AutoAssignmentService autoAssignmentService;
    private final ProjectAccessService projectAccessService;

    @Transactional
    public BugResponse createBug(CreateBugRequest request){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User tester= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));

        Project project= projectRepository.findById(request.getProjectId()).orElseThrow(()->new RuntimeException("Project not found"));
        projectAccessService.requireProjectAccess(project.getProjectId());

        BugStatus openStatus= bugStatusRepository.findByStatusName("Open").orElseThrow(()->new RuntimeException("Status not found"));

        Priority priority= priorityRepository.findByPriorityName(request.getPriorityName()).orElseThrow(()-> new RuntimeException("Priority not found: "+ request.getPriorityName()));

        Severity severity= severityRepository.findBySeverityName(request.getSeverityName()).orElseThrow(()->new RuntimeException("Severity not found: "+ request.getSeverityName()));

        List<DuplicateResult> duplicateResult = duplicateDetectionService.findSimilarBugs(request.getProjectId(), request.getTitle());
        if(!duplicateResult.isEmpty()){
            System.out.println("Warning: "+ duplicateResult.size()+"similar bug found "+ request.getTitle());
            duplicateResult.forEach(d-> System.out.println("  - "+d.getBug().getTitle()+" ("+d.getSimilarityPercent()+ "% similar"));
        }

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
        try{
            User developer= autoAssignmentService.findLeastLoadedDeveloper(request.getProjectId());
            BugStatus assignedStatus= bugStatusRepository.findByStatusName("Assigned").orElseThrow();
            saved.setAssignedTo(developer);
            saved.setStatus(assignedStatus);
            saved= bugRepository.save(saved);

            sendNotification(developer,saved,"new bug auto-assigned to You: "+ saved.getTitle());

            logStatusHistory(saved,openStatus,assignedStatus,tester,"Auto-assigned to "+ developer.getFullName());

            System.out.println("bug auto-assigned to: "+ developer.getFullName());
        }catch (Exception e){
            System.out.println("Auto-assigned failed: "+e.getMessage()+" -- Bug left as Open");
        }
        logStatusHistory(saved, null, openStatus, tester, "Bug raised");
        return mapToResponse(saved);
    }

    public BugResponse mapToResponse(Bug bug) { //map to response use for entity to DTO.
        return BugResponse.builder()
                .bugId(bug.getBugId())
                .title(bug.getTitle())
                .description(bug.getDescription())
                .stepsToReproduce(bug.getStepsToReproduce())
                .projectId(bug.getProject() != null ? bug.getProject().getProjectId() : null)
                .projectName(bug.getProject().getProjectName())
                .raisedBy(bug.getRaisedBy().getFullName())
                .assignedTo(bug.getAssignedTo() != null
                        ? bug.getAssignedTo().getFullName() : "Unassigned")
                .assignedToEmail(bug.getAssignedTo() != null ? bug.getAssignedTo().getEmail() : null)
                .assignedToUserId(bug.getAssignedTo() != null ? bug.getAssignedTo().getUserId() : null)
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
        projectAccessService.requireBugAccess(bug);

        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User changedBy= userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found"));
        if ("Developer".equals(changedBy.getRole().getRoleName())) {
            if (bug.getAssignedTo() == null || !bug.getAssignedTo().getUserId().equals(changedBy.getUserId())) {
                throw new RuntimeException("Only the assigned developer can change this bug status");
            }
        }

        BugStatus newStatus = resolveStatus(request.getStatusName());

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
    @Transactional
    public BugResponse assignBug(Integer bugid, Integer developerId){
        Bug bug= bugRepository.findById(bugid)
                .orElseThrow(()-> new RuntimeException("Bug not found: "+ bugid));

        User developer= userRepository.findById(developerId).orElseThrow(()-> new RuntimeException("Developer not found: "+ developerId));
        if (!Boolean.TRUE.equals(developer.getIsActive())) {
            throw new RuntimeException("Cannot assign work to a deactivated user");
        }
        if (!"Developer".equals(developer.getRole().getRoleName())) {
            throw new RuntimeException("Selected user is not a Developer");
        }

        BugStatus bugStatus= bugStatusRepository.findByStatusName("Assigned").orElseThrow(()-> new RuntimeException("Status not found"));

        BugStatus oldStatus= bug.getStatus();
        bug.setAssignedTo(developer);
        bug.setStatus(bugStatus);

        Bug updated= bugRepository.save(bug);

        String email= SecurityContextHolder.getContext().getAuthentication().getName();

        User assignedBy= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        logStatusHistory(updated, oldStatus, bugStatus, assignedBy, "Bug Assigned to "+ developer.getFullName());

        sendNotification(developer, bug, "New bug assigned to you: "+ bug.getTitle());
        return mapToResponse(updated);
    }

    public List<BugResponse> getBugsByProject(Integer projectId) { //getting all bugs by project name and project id.
        projectAccessService.requireProjectAccess(projectId);
        return bugRepository.findByProject_ProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> getMyAssignedBugs() { //for developer in which developer can see their own assigned bug list.
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User developer = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
        return bugRepository
                .findByAssignedTo_UserId(developer.getUserId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> getMyRaisedBugs() { //for Tester , see the tester see list of bug which is raised by self.
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
        return bugRepository
                .findByRaisedBy_UserId(tester.getUserId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BugResponse getBugById(Integer bugId){
        Bug bug= bugRepository.findById(bugId).orElseThrow(()->new RuntimeException("Bug not found: "+ bugId));
        projectAccessService.requireBugAccess(bug);
        return mapToResponse(bug);
    }

    public java.util.Map<String, Object> checkDuplicates(Integer projectId, String title) {
        projectAccessService.requireProjectAccess(projectId);
        List<DuplicateResult> results = duplicateDetectionService.findSimilarBugs(projectId, title);
        if (results.isEmpty()) {
            return java.util.Map.of("hasDuplicates", false, "message", "No Similar bug found", "similarBugs", results);
        }
        return java.util.Map.of(
                "hasDuplicates", true,
                "message", results.size() + " similar bugs found. please review.",
                "similarBugs", results
        );
    }

    public List<com.bugtrack.bugtrack.dto.response.BugHistoryResponse> getBugHistory(Integer bugId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug not found: " + bugId));
        projectAccessService.requireBugAccess(bug);
        return historyRepository.findByBug_BugIdOrderByChangedAtAsc(bugId)
                .stream()
                .map(this::mapHistory)
                .collect(Collectors.toList());
    }

    private com.bugtrack.bugtrack.dto.response.BugHistoryResponse mapHistory(BugStatusHistory history) {
        return new com.bugtrack.bugtrack.dto.response.BugHistoryResponse(
                history.getHistoryId(),
                history.getOldStatus() != null ? history.getOldStatus().getStatusName() : "-",
                history.getNewStatus() != null ? history.getNewStatus().getStatusName() : "-",
                history.getChangedBy() != null ? history.getChangedBy().getFullName() : "System",
                history.getRemarks(),
                history.getChangedAt()
        );
    }

    private BugStatus resolveStatus(String statusName) {
        if (statusName == null || statusName.isBlank()) {
            throw new RuntimeException("Status is required");
        }
        String trimmed = statusName.trim();
        return bugStatusRepository.findByStatusName(trimmed)
                .or(() -> bugStatusRepository.findByStatusName(mapStatusAlias(trimmed)))
                .orElseThrow(() -> new RuntimeException("Status not found: " + trimmed));
    }

    private String mapStatusAlias(String statusName) {
        return switch (statusName.toLowerCase().replace(" ", "").replace("_", "").replace("-", "")) {
            case "retesting" -> "Re-Testing";
            case "inprogress" -> "In Progress";
            default -> statusName;
        };
    }

}
