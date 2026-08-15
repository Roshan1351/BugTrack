package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugResponse {
    private Integer bugId;
    private String title;
    private String description;
    private String stepsToReproduce;
    private String projectName;
    private String raisedBy;
    private String assignedTo;
    private String status;
    private String priority;
    private String severity;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
