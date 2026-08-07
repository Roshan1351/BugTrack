package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "Name is required")
    private String fullName;
    private Boolean isActive;
}
