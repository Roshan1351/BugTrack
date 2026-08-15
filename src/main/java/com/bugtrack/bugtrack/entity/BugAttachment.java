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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bug_attachments")
public class BugAttachment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer attachmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bug_id", nullable = false)
    private Bug bug;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate(){
        this.uploadedAt= LocalDateTime.now();
    }
}
