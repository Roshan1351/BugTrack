package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProjectRequest {
    @NotBlank(message = "project name is required")
    private String projectName;
    private String description;
}
