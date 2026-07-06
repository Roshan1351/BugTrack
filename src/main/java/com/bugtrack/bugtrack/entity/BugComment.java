package com.bugtrack.bugtrack.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bug_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BugComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id",
            nullable = false)
    private Bug bug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commented_by",
            nullable = false)
    private User commentedBy;

    @Column(name = "comment_text",
            nullable = false,
            columnDefinition = "TEXT")
    private String commentText;

    @Column(name = "created_at",
            updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
