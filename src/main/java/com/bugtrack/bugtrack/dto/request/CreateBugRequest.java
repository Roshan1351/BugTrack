package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBugRequest {
    @NotNull(message = "Project ID is required")
    private Integer projectId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String stepsToReproduce;

    @NotBlank(message = "priority is required")
    private String priorityName;
    @NotBlank(message = "Severity is required")
    private String severityName;
}
