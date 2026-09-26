package com.bugtrack.bugtrack.controller;

import com.bugtrack.bugtrack.dto.request.CreateUserRequest;
import com.bugtrack.bugtrack.dto.request.ResetPasswordRequest;
import com.bugtrack.bugtrack.dto.request.UpdateUserRequest;
import com.bugtrack.bugtrack.dto.response.UserResponse;
import com.bugtrack.bugtrack.dto.response.UserWorkloadResponse;
import com.bugtrack.bugtrack.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // create user which is only allowed to admin
    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    //get all users by admin
    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //get user by role
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<List<UserResponse>> getUserbyRole(@PathVariable String roleName){
        return ResponseEntity.ok(userService.getUserByRole(roleName));
    }

    //get users by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id){
        return ResponseEntity.ok(userService.getUserById(id));
    }


    //update user details
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @Valid @RequestBody UpdateUserRequest request){
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    //deactivate uesr it means disable user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> deactivateUser(@PathVariable Integer id){
        userService.deactivateUser(id);
        return ResponseEntity.ok("user deactivate successfully");
    }

    //activate user
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserResponse> activateUser(
            @PathVariable Integer id) {

        return ResponseEntity.ok(userService.activateUser(id));
    }

    //reset password
    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<String> resetPassword(
            @PathVariable Integer id,
            @RequestBody ResetPasswordRequest request) {

        userService.resetPassword(id, request);

        return ResponseEntity.ok("Password reset successfully");
    }

    //get all user workload list.
    @GetMapping("/workload")
    @PreAuthorize("hasRole('Admin') or hasRole('Project Manager')")
    public ResponseEntity<List<UserWorkloadResponse>> getAllUsersWorkload() {
        return ResponseEntity.ok(userService.getAllUsersWorkload());
    }
}
