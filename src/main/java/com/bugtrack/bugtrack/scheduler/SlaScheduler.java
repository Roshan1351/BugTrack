package com.bugtrack.bugtrack.scheduler;

import com.bugtrack.bugtrack.entity.Bug;
import com.bugtrack.bugtrack.entity.Notification;
import com.bugtrack.bugtrack.repository.BugRepository;
import com.bugtrack.bugtrack.repository.NotificationRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class SlaScheduler {
    private final BugRepository bugRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000) //1 minute
    public void checkSlaBreaches(){
        System.out.println("Sla check running at: "+ LocalDateTime.now());

        List<Bug> overdueBugs= bugRepository.findOverdueBugs(LocalDateTime.now());

        if(overdueBugs.isEmpty()){
            System.out.println("No overdue bugs found");
            return;
        }

        System.out.println("found "+ overdueBugs.size()+" overdue bugs!");
        for(Bug bug: overdueBugs){
            System.out.println("Overdue: ["+bug.getBugId()+"] "+ bug.getTitle() +" | Due: "+bug.getDueDate()+" | Priority: "+ bug.getPriority().getPriorityName());

            userRepository.findByRole_RoleName("Admin").forEach(admin->{
                boolean alreadyNotified= notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(admin.getUserId()).stream().anyMatch(n->n.getBug().getBugId().equals(bug.getBugId())&& n.getMessage().contains("sla breach"));
                if(!alreadyNotified){
                    Notification notification= new Notification();
                    notification.setUser(admin);
                    notification.setBug(bug);
                    notification.setMessage("sla breach: bug # "+ bug.getBugId()+ " overdue! priority: "+ bug.getPriority().getPriorityName()+ " | Title: "+ bug.getTitle());
                    notification.setIsRead(false);
                    notificationRepository.save(notification);
                    System.out.println("Escalation sent to admin: "+ admin.getEmail());
                }
            });
        }
    }

}
