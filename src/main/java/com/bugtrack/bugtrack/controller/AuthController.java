package com.bugtrack.bugtrack.controller;

import com.bugtrack.bugtrack.dto.request.LoginRequest;
import com.bugtrack.bugtrack.dto.request.RegisterRequest;
import com.bugtrack.bugtrack.dto.response.AuthResponse;
import com.bugtrack.bugtrack.service.Authservice;
import com.bugtrack.bugtrack.dto.request.ChangePasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final Authservice authservice;

    //register the user by Admin only
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authservice.register(request));
    }

    //login user
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authservice.login(request));
    }

    //change password
    @PostMapping("/change-password")
    public <ChangePasswordRequest> ResponseEntity<AuthResponse> changePassword(@Valid @RequestBody com.bugtrack.bugtrack.dto.request.ChangePasswordRequest request){
        return ResponseEntity.ok(authservice.changePassword(request));
    }
}
