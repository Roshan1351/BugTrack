package com.bugtrack.bugtrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String newPassword;
}
