package com.bugtrack.bugtrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NotificationResponse {
    private Integer notificationId;
    private Integer bugId;
    private String bugTitle;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

}
