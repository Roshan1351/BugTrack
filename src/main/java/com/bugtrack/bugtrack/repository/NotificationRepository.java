package com.bugtrack.bugtrack.repository;

import com.bugtrack.bugtrack.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser_UserIdAndIsReadFalse(Integer userId);

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Integer userId);

    long countByUser_UserIdAndIsReadFalse(Integer userId);
}
