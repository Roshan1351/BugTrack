package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Integer projectId;
    private String projectName;
    private String description;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
}
