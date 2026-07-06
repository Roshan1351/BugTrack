package com.bugtrack.bugtrack.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false)
    private User user;                     // Notification kis user ko jaayegi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id",
            nullable = false)
    private Bug bug;

    @Column(name = "message",
            nullable = false,
            length = 255)
    private String message;

    @Column(name = "is_read")
    private Boolean isRead = false;        // Default unread

    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}