package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private Integer commentId;
    private Integer bugId;
    private String commentedBy;
    private String commentText;
    private LocalDateTime createdAt;
}
