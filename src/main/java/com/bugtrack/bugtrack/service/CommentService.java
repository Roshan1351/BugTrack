package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.request.CreateCommentRequest;
import com.bugtrack.bugtrack.dto.response.BugCommentResponse;
import com.bugtrack.bugtrack.dto.response.CommentResponse;
import com.bugtrack.bugtrack.entity.Bug;
import com.bugtrack.bugtrack.entity.BugComment;
import com.bugtrack.bugtrack.entity.Notification;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.BugCommentRepository;
import com.bugtrack.bugtrack.repository.BugRepository;
import com.bugtrack.bugtrack.repository.NotificationRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final BugCommentRepository bugCommentRepository;
    private final BugRepository bugRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public CommentResponse addComment(Integer bugid, CreateCommentRequest request){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user= userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));

        Bug bug= bugRepository.findById(bugid).orElseThrow(()->new RuntimeException("Bug not found with id: "+ bugid));

        BugComment bugcomment= new BugComment();
        bugcomment.setBug(bug);
        bugcomment.setCommentedBy(user);
        bugcomment.setCommentText(request.getCommentText());

        BugComment saved= bugCommentRepository.save(bugcomment);

        notifyOtherParty(user, bug);
        return maptoResponse(saved);
    }

    private void notifyOtherParty(User user, Bug bug) {
        User recipient= null;
        String message= null;

        if(user.getRole().getRoleName().equals("Developer")){
            recipient= bug.getRaisedBy();
            message= "Developer commented on your bug: "+ bug.getTitle();


        }else if(user.getRole().getRoleName().equals("Tester")){
            recipient= bug.getAssignedTo();
            message="Tester commented on bug: "+ bug.getTitle();
        }

        if(recipient!= null && message!= null && !recipient.getUserId().equals(user.getUserId())){
            Notification notification= new Notification();
            notification.setUser(recipient);
            notification.setBug(bug);
            notification.setMessage(message);
            notification.setIsRead(false);
            notificationRepository.save(notification);
        }

    }

    private CommentResponse maptoResponse(BugComment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .bugId(comment.getBug().getBugId())
                .commentedBy(comment.getCommentedBy().getFullName())
                .commentText(comment.getCommentText())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public List<CommentResponse> getCommentsByBugs(Integer bugId) {

        bugRepository.findById(bugId).orElseThrow(()->new RuntimeException("Bug not found with id: "+ bugId));
        return bugCommentRepository.findByBug_BugIdOrderByCreatedAtAsc(bugId).stream().map(this::maptoResponse).collect(Collectors.toList());
    }

}
