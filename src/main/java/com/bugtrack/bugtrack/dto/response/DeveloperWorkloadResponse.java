package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperWorkloadResponse {
    private Integer userId;
    private String fullName;
    private String email;
    private Boolean isActive;
    private long openBugs;
}
