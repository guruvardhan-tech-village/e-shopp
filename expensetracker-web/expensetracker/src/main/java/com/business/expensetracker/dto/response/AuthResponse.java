package com.business.expensetracker.dto.response;

/**
 * Response DTO returned after successful registration or login.
 * Requirements: 1.1, 1.4
 */
public record AuthResponse(
        String token,
        String email,
        String displayName,
        Long userId
) {}
