package com.business.expensetracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user registration.
 * Requirements: 1.1, 1.3
 */
public record RegisterRequest(

        @Email(message = "Email must be a valid email address")
        @NotBlank(message = "Email is required")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Display name is required")
        String displayName
) {}
