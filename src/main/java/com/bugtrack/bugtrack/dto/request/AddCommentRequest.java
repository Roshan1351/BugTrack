package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {
    @NotBlank(message = "Comment cannot be empty")
    private String commentText;
}
