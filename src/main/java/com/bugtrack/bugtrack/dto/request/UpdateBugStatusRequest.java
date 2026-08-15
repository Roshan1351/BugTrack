package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBugStatusRequest {
    @NotNull(message = "Status ID is required")
    private String statusName;

    private String remark;
}
