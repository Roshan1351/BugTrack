package com.bugtrack.bugtrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BugStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bug_id")
    private Bug bug;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "old_status_id")
    private BugStatus oldStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "new_status_id")
    private BugStatus newStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected  void onCreate(){
        this.changedAt= LocalDateTime.now();
    }
}
