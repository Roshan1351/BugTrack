package com.bugtrack.bugtrack.service;

import com.bugtrack.bugtrack.dto.request.LoginRequest;
import com.bugtrack.bugtrack.dto.request.RegisterRequest;
import com.bugtrack.bugtrack.dto.response.AuthResponse;
import com.bugtrack.bugtrack.entity.Role;
import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.RoleRepository;
import com.bugtrack.bugtrack.repository.UserRepository;
import com.bugtrack.bugtrack.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Authservice {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already registered: "+ request.getEmail());
        }

        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(()->
                new RuntimeException(
                        "Role not found : "+ request.getRoleName()
                ));

        User user= new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setIsActive(true);

        userRepository.save(user);

        UserDetails userDetails= userDetailsService.loadUserByUsername(user.getEmail());
        String token= jwtUtil.generatetoken(userDetails, role.getRoleName());
        return new AuthResponse(token, user.getEmail(), role.getRoleName(), user.getFullName(), "Registration Successful");
    }

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user= userRepository.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException("user not found"));

        UserDetails userDetails= userDetailsService.loadUserByUsername(user.getEmail());
        String token= jwtUtil.generatetoken(userDetails, user.getRole().getRoleName());

        return new AuthResponse(
                token, user.getEmail(), user.getRole().getRoleName(),
                user.getFullName(), "Login successful"
        );
    }
}
