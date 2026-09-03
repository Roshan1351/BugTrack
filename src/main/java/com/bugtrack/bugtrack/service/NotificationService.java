package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.response.NotificationResponse;
import com.bugtrack.bugtrack.entity.Notification;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.NotificationRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    UserRepository userRepository;

    public List<NotificationResponse> getMyNotification(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId()).stream().map(this::maptoResponse).collect(Collectors.toList());
    }

    private NotificationResponse maptoResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .bugId(notification.getBug().getBugId())
                .bugTitle(notification.getBug().getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public List<NotificationResponse> getUnreadNotification(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        return notificationRepository.findByUser_UserIdAndIsReadFalse(user.getUserId()).stream()
                .map(this::maptoResponse).collect(Collectors.toList());
    }

    public long getUnreadNotificationCount(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));

        return notificationRepository.countByUser_UserIdAndIsReadFalse(user.getUserId());
    }

    //mark as read notification
    @Transactional
    public void markAsRead(Integer notificationId){
        Notification notification= notificationRepository.findById(notificationId).orElseThrow(()->new RuntimeException("Notification not found: "+ notificationId));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    //all notification to marks as read
    @Transactional
    public void markAllAsRead(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));

        List<Notification> unread= notificationRepository.findByUser_UserIdAndIsReadFalse(user.getUserId());
        unread.forEach(n->n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }


}
