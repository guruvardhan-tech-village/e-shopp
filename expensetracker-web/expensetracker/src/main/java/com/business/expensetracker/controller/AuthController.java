package com.business.expensetracker.controller;

import com.business.expensetracker.dto.request.LoginRequest;
import com.business.expensetracker.dto.request.RegisterRequest;
import com.business.expensetracker.dto.response.ApiResponse;
import com.business.expensetracker.dto.response.AuthResponse;
import com.business.expensetracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles user registration and authentication.
 *
 * <ul>
 *   <li>{@code POST /api/v1/auth/register} — create a new account, returns 201 Created</li>
 *   <li>{@code POST /api/v1/auth/login}    — authenticate, returns 200 OK with JWT</li>
 * </ul>
 *
 * Requirements: 1.1, 1.4, 10.1, 10.2
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account.
     *
     * @param request validated registration payload
     * @return 201 Created with {@link AuthResponse} wrapped in {@link ApiResponse}
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse authResponse = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authResponse));
    }

    /**
     * Authenticate an existing user and return a JWT.
     *
     * @param request validated login payload
     * @return 200 OK with {@link AuthResponse} wrapped in {@link ApiResponse}
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse.success("Login successful", authResponse));
    }
}
