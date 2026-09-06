package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponse {
    private Integer attachmentId;
    private Integer bugId;
    private String fileUrl;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
