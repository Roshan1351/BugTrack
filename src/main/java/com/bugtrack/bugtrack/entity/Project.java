package com.bugtrack.bugtrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "project_name",nullable = false)
    private String projectName;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProjectStatus status= ProjectStatus.Active;

    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt= LocalDateTime.now();
    }

    public enum ProjectStatus{
        Active,
        Completed,
        onHold
    }
}
