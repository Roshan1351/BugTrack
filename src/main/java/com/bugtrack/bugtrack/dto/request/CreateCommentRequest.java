package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank(message = "Comment text is required")
    private String commentText;
}
