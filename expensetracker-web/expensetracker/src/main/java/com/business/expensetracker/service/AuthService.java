package com.business.expensetracker.service;

import com.business.expensetracker.dto.request.LoginRequest;
import com.business.expensetracker.dto.request.RegisterRequest;
import com.business.expensetracker.dto.response.AuthResponse;
import com.business.expensetracker.exception.DuplicateResourceException;
import com.business.expensetracker.model.User;
import com.business.expensetracker.repository.UserRepository;
import com.business.expensetracker.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and authentication.
 * Requirements: 1.1, 1.2, 1.4, 1.5, 9.3
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // -------------------------------------------------------------------------
    // Register
    // -------------------------------------------------------------------------

    /**
     * Registers a new user account.
     *
     * <ol>
     *   <li>Checks for duplicate email — throws {@link DuplicateResourceException} if found.</li>
     *   <li>Hashes the password with BCrypt (cost ≥ 10 as configured in {@code SecurityConfig}).</li>
     *   <li>Persists the new {@link User} entity.</li>
     *   <li>Returns an {@link AuthResponse} containing a signed JWT.</li>
     * </ol>
     *
     * @param request validated registration payload
     * @return auth response with JWT and user details
     * @throws DuplicateResourceException if the email is already registered
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "An account with email '" + request.email() + "' already exists");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .build();

        User saved = userRepository.save(user);
        log.info("Registered new user id={} email={}", saved.getId(), saved.getEmail());

        String token = jwtTokenProvider.generateToken(saved.getId());
        return buildAuthResponse(saved, token);
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    /**
     * Authenticates an existing user.
     *
     * <ol>
     *   <li>Loads the user by email — throws {@link BadCredentialsException} if not found.</li>
     *   <li>Verifies the BCrypt hash — throws {@link BadCredentialsException} if mismatch.</li>
     *   <li>Generates a JWT via {@link JwtTokenProvider#generateToken(Long)}.</li>
     *   <li>Returns an {@link AuthResponse}.</li>
     * </ol>
     *
     * @param request validated login payload
     * @return auth response with JWT and user details
     * @throws BadCredentialsException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        log.info("User id={} logged in", user.getId());
        return buildAuthResponse(user, token);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private AuthResponse buildAuthResponse(User user, String token) {
        return new AuthResponse(token, user.getEmail(), user.getDisplayName(), user.getId());
    }
}
