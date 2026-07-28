package com.bugtrack.bugtrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bugs", indexes={
        @Index(name = "idx_bug_Project", columnList = "project_id"),
        @Index(name = "idx_bug_assigned", columnList = "assigned_to"),
        @Index(name = "idx_bug_status", columnList = "status_id")
})
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bugId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "step_to_reproduce", columnDefinition = "TEXT")
    private String stepsToReproduce;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by", nullable = false)
    private User raisedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private BugStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "priority_id")
    private Priority priority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "severity_id")
    private Severity severity;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;


    @PrePersist
    protected  void onCreate(){
        this.createdAt= LocalDateTime.now();
        this.updatedAt= LocalDateTime.now();
    }

    @PreUpdate
    protected  void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
