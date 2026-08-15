package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BugCommentResponse {
    private Integer commentId;
    private String commentText;
    private String commentedBy;
    private LocalDateTime commentedAt;
}
