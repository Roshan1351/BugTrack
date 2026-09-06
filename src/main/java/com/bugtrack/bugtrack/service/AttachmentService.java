package com.bugtrack.bugtrack.service;


import com.bugtrack.bugtrack.dto.response.AttachmentResponse;
import com.bugtrack.bugtrack.entity.Bug;
import com.bugtrack.bugtrack.entity.BugAttachment;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.BugAttachmentRepository;
import com.bugtrack.bugtrack.repository.BugRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttachmentService {
    @Autowired
    private BugAttachmentRepository bugAttachmentRepository;
    @Autowired
    private BugRepository bugRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;


    public AttachmentResponse uploadAttachment(Integer bugId, MultipartFile file){
        fileStorageService.validateFile(file);
        Bug bug= bugRepository.findById(bugId).orElseThrow(()->new RuntimeException("Bug not found with id: "+ bugId));

        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));

        String fileUrl= fileStorageService.saveFile(file);

        BugAttachment attachment= new BugAttachment();
        attachment.setBug(bug);
        attachment.setFileUrl(fileUrl);
        attachment.setUploadedBy(user);
        BugAttachment saved= bugAttachmentRepository.save(attachment);
        return mapToResponse(saved);
    }

    private AttachmentResponse mapToResponse(BugAttachment saved) {
        return AttachmentResponse.builder().attachmentId(saved.getAttachmentId()).bugId(saved.getBug().getBugId()).fileUrl(saved.getFileUrl())
                .uploadedBy(saved.getUploadedBy().getFullName())
                .uploadedAt(saved.getUploadedAt()).build();
    }

    public List<AttachmentResponse> getAttachments(Integer bugId){
        bugRepository.findById(bugId).orElseThrow(()->new RuntimeException("Bug not found with id: "+ bugId));
        return bugAttachmentRepository.findByBug_BugId(bugId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }
}
