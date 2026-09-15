package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.request.CreateUserRequest;
import com.bugtrack.bugtrack.dto.request.ResetPasswordRequest;
import com.bugtrack.bugtrack.dto.request.UpdateUserRequest;
import com.bugtrack.bugtrack.dto.response.UserResponse;
import com.bugtrack.bugtrack.dto.response.UserWorkloadResponse;
import com.bugtrack.bugtrack.entity.Role;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.BugRepository;
import com.bugtrack.bugtrack.repository.RoleRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import io.jsonwebtoken.security.Password;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final BugRepository bugRepository;

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getIsActive()
        );
    }

    public UserResponse createUser(CreateUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("email already exists: "+ request.getEmail());
        }
        Role role= roleRepository.findByRoleName(request.getRoleName()).orElseThrow(()->new RuntimeException("Role not found: "+ request.getRoleName()));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(role);
        user.setIsActive(true);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        User saved= userRepository.save(user);
        return mapToResponse(saved);
    }

    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream().map(this::mapToResponse)
                .collect(Collectors.toList());

    }

    public List<UserResponse> getUserByRole(String role){
        return userRepository.findByRole_RoleName(role).stream()
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Integer id){
        User user= userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found: "+ id));
        return mapToResponse(user);
    }

    public UserResponse updateUser(Integer id, UpdateUserRequest request){
        User user= userRepository.findById(id).orElseThrow(()->
                new RuntimeException(("User not found: "+ id)
        ));

        user.setFullName(request.getFullName());
        if(request.getIsActive()!= null){
            user.setIsActive(request.getIsActive());
        }
        User updated= userRepository.save(user);
        return mapToResponse(updated);
    }

    public void deactivateUser(Integer id){
        User user= userRepository.findById(id).orElseThrow(()->
                new RuntimeException("user not found: "+ id));
        String roleName = user.getRole().getRoleName();
        if ("Admin".equals(roleName) || "Project Manager".equals(roleName)) {
            throw new RuntimeException("Admin and Project Manager accounts cannot be deactivated");
        }
        user.setIsActive(false);
        userRepository.save(user);
    }

    public UserResponse activateUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("user not found: " + id));
        user.setIsActive(true);
        return mapToResponse(userRepository.save(user));
    }

    public void resetPassword(Integer id, ResetPasswordRequest request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new RuntimeException("user not found: " + id));
        String roleName = user.getRole().getRoleName();
        if ("Admin".equals(roleName) || "Project Manager".equals(roleName)) {
            throw new RuntimeException("Cannot reset Admin or Project Manager password from here");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Admin workload table ke liye — sabhi users ka bug summary
    public List<UserWorkloadResponse> getAllUsersWorkload() {
        List<User> users = userRepository.findAll();

        // Bulk query se ek baar mein sabka data lo — N+1 problem avoid karo
        List<Object[]> workloadStats = bugRepository.findAllUserWorkloadStats();

        // userId -> [total, active, completed] map banao
        Map<Integer, long[]> statsMap = new HashMap<>();
        for (Object[] row : workloadStats) {
            Integer uid    = (Integer) row[0];
            long total     = ((Number) row[1]).longValue();
            long active    = ((Number) row[2]).longValue();
            long completed = ((Number) row[3]).longValue();
            statsMap.put(uid, new long[]{total, active, completed});
        }

        return users.stream().map(user -> {
            long[] stats = statsMap.getOrDefault(user.getUserId(), new long[]{0, 0, 0});
            return UserWorkloadResponse.builder()
                    .userId(user.getUserId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .role(user.getRole().getRoleName())
                    .isActive(user.getIsActive())
                    .totalBugs(stats[0])
                    .activeBugs(stats[1])
                    .completedBugs(stats[2])
                    .build();
        }).collect(Collectors.toList());
    }
}
