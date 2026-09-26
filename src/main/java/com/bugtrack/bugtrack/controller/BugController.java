package com.bugtrack.bugtrack.controller;

import com.bugtrack.bugtrack.dto.request.CreateBugRequest;
import com.bugtrack.bugtrack.dto.request.UpdateBugStatusRequest;
import com.bugtrack.bugtrack.dto.response.BugResponse;
import com.bugtrack.bugtrack.dto.response.DuplicateResult;
import com.bugtrack.bugtrack.dto.response.UserWorkloadResponse;
import com.bugtrack.bugtrack.entity.Bug;
import com.bugtrack.bugtrack.service.BugService;
import com.bugtrack.bugtrack.service.DuplicateDetectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
public class BugController {
    private final BugService bugService;

    //create bug by tester
    @PostMapping
    @PreAuthorize("hasRole('Tester')")
    public ResponseEntity<BugResponse> createBug(@Valid @RequestBody CreateBugRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bugService.createBug(request));
    }

    //assigned to developer by automatic by checking workload of each developer
    @PatchMapping("/{bugid}/assign/{developerid}")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<BugResponse> assignBug(@PathVariable Integer bugid, @PathVariable Integer developerid){
        return ResponseEntity.ok(bugService.assignBug(bugid, developerid));
    }

    //update bug means change status of bug like assigned to , resolved , re-testing, closed, etc.
    @PatchMapping("/{bugId}/status")
    @PreAuthorize("hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<BugResponse> updateBug(@PathVariable Integer bugId, @Valid @RequestBody UpdateBugStatusRequest request){
        return ResponseEntity.ok(bugService.updateBugStatus(bugId, request));
    }

    //get bugs by project id where all users can see the bug list
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')" +
            " or hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<List<BugResponse>> getBugsByProject(@PathVariable Integer projectId){
        return ResponseEntity.ok(bugService.getBugsByProject(projectId));
    }

    //developer can see own bug assigned list
    @GetMapping("/my-assigned")
    @PreAuthorize("hasRole('Developer')")
    public ResponseEntity<List<BugResponse>> getMyAssignedBugs(){ //list of bug which is assigned to developer
        return ResponseEntity.ok(bugService.getMyAssignedBugs());
    }

    //tester can see the raised by tester own.
    @GetMapping("/my-raised")
    @PreAuthorize("hasRole('Tester')")
    public ResponseEntity<List<BugResponse>> getMyRaisedBugs(){
        return ResponseEntity.ok(bugService.getMyRaisedBugs());
    }

    //getting bug history
    @GetMapping("/{bugId}/history")
    @PreAuthorize("hasRole('Admin') or hasRole('Developer') or hasRole('Project Manager') or hasRole('Tester')")
    public ResponseEntity<List<com.bugtrack.bugtrack.dto.response.BugHistoryResponse>> getBugHistory(@PathVariable Integer bugId){
        return ResponseEntity.ok(bugService.getBugHistory(bugId));
    }

    //getting bug details by bug id.
    @GetMapping("/{bugId}")
    @PreAuthorize("hasRole('Admin') or hasRole('Developer') or hasRole('Project Manager') or hasRole('Tester')")
    public ResponseEntity<BugResponse> getBugById(@PathVariable Integer bugId){
        return ResponseEntity.ok(bugService.getBugById(bugId));
    }

    //check duplicate bug title
    @GetMapping("/duplicate-check")
    @PreAuthorize("hasRole('Tester')")
    public ResponseEntity<?> checkduplicate(@RequestParam Integer projectId, @RequestParam String title){
        return ResponseEntity.ok(bugService.checkDuplicates(projectId, title));
    }

}