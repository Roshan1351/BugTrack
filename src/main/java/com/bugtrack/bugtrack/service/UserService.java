package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.request.CreateUserRequest;
import com.bugtrack.bugtrack.dto.request.UpdateUserRequest;
import com.bugtrack.bugtrack.dto.response.UserResponse;
import com.bugtrack.bugtrack.entity.Role;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.RoleRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import io.jsonwebtoken.security.Password;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
        return userRepository.findByRole_RoleName(role).stream().map(this::mapToResponse)
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
        user.setIsActive(false);
        userRepository.save(user);
    }
}
