package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignBugRequest {
    @NotNull(message = "Developer user ID is required")
    private Integer assignedTo;
}
