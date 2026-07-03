package com.bugtrack.bugtrack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "severities")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Severity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="severity_id")
    private Integer severityId;
    @Column(name = "severity_name",nullable = false,unique = true)
    private String severityName;
}
