package com.bugtrack.bugtrack.controller;

import com.bugtrack.bugtrack.dto.response.NotificationResponse;
import com.bugtrack.bugtrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    //get my notification all user can see their own notification.
    @GetMapping
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')" +
            " or hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<List<NotificationResponse>> getMyNotification(){
        return ResponseEntity.ok(notificationService.getMyNotification());
    }

    //get unread notification which is not read by their own.
    @GetMapping("/unread")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')" +
            " or hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotification(){
        return ResponseEntity.ok(notificationService.getUnreadNotification());
    }

    //count of notification which is not read
    @GetMapping("/count")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')" +
            " or hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<Map<String , Long>> getUnreadCount(){
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.getUnreadNotificationCount()));
    }

    //marking a notification as read
    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')" +
            " or hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<String> markAsRead(@PathVariable Integer notificationId){
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }

    //marking all notification as read.
    @PatchMapping("/read-all")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')" +
            " or hasRole('Developer') or hasRole('Tester')")
    public ResponseEntity<String> markAllRead(){
        notificationService.markAllAsRead();
        return ResponseEntity.ok("All notification marked as read");
    }
}
