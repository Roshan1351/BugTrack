package com.bugtrack.bugtrack.controller;

import com.bugtrack.bugtrack.dto.request.CreateCommentRequest;
import com.bugtrack.bugtrack.dto.response.CommentResponse;
import com.bugtrack.bugtrack.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs/{bugId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('Developer') or hasRole('Tester') or hasRole('Admin')")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Integer bugId, @Valid @RequestBody CreateCommentRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(bugId,request));

    }

    @GetMapping
    @PreAuthorize("hasRole('Developer') or hasRole('Tester') or hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<List<CommentResponse>> getcomments(@PathVariable Integer bugId){
        return ResponseEntity.ok(commentService.getCommentsByBugs(bugId));
    }
}
