package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugHistoryResponse {
    private Integer historyId;
    private String oldStatus;
    private String newStatus;
    private String changedBy;
    private String comment;
    private LocalDateTime changedAt;
}
