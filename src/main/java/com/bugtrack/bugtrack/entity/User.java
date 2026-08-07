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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "full_name",nullable = false)
    private String fullName;

    @Column(name = "email",nullable = false,unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id",nullable = false)
    private Role role;

    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive= true;

    @Column(nullable = false, length = 255, name = "password_hash")
    private String passwordHash;

    @PrePersist
    protected  void onCreate(){
        this.createdAt= LocalDateTime.now();
    }

}
