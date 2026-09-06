package com.bugtrack.bugtrack.controller;

import com.bugtrack.bugtrack.dto.response.AttachmentResponse;
import com.bugtrack.bugtrack.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/bugs/{bugId}/attachments")
public class AttachmentController {
    @Autowired
    private AttachmentService attachmentService;

    @PostMapping //upload our screenshot
    @PreAuthorize("hasRole('Tester') or hasRole('Developer') or hasRole('Admin')")
    public ResponseEntity<AttachmentResponse> uploadAttachment(@PathVariable Integer bugId, @RequestParam("file") MultipartFile file){
        return ResponseEntity.status(HttpStatus.CREATED).body(attachmentService.uploadAttachment(bugId, file));
    }

    @GetMapping //bug attachment getting
    @PreAuthorize("hasRole('Tester') or hasRole('Developer') or hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(@PathVariable Integer bugId){
        return ResponseEntity.ok(attachmentService.getAttachments(bugId));
    }
}
