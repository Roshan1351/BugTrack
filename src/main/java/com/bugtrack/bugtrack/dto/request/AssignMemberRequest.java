package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignMemberRequest {
    @NotNull(message = "user id is required")
    private Integer userId;
    @NotNull(message = "Role is required")
    private String roleInProject; //developer or tester
}
