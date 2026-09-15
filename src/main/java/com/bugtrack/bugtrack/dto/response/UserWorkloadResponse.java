package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkloadResponse {
    private Integer userId;
    private String fullName;
    private String email;
    private String role;
    private Boolean isActive;
    private long totalBugs;
    private long activeBugs;   // Open + Assigned + In Progress + Reopened + Re-Testing
    private long completedBugs; // Resolved + Closed
}
