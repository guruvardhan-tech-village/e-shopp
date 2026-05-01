package com.business.expensetracker.config;

import com.business.expensetracker.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Central Spring Security configuration.
 *
 * <ul>
 *   <li>Stateless JWT-based authentication (no HTTP session)</li>
 *   <li>CSRF disabled (not needed for stateless REST APIs)</li>
 *   <li>CORS configured via {@link CorsConfig}</li>
 *   <li>{@code POST /api/v1/auth/**} is publicly accessible</li>
 *   <li>All other {@code /api/v1/**} routes require a valid JWT</li>
 *   <li>{@link JwtAuthenticationFilter} runs before the standard username/password filter</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — stateless JWT API does not need it
            .csrf(AbstractHttpConfigurer::disable)

            // Apply CORS configuration from CorsConfig
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // Stateless session — no HttpSession created or used
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                    // Auth endpoints are public
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                    // All other API routes require authentication
                    .requestMatchers("/api/v1/**").authenticated()
                    // Everything else (actuator, static resources, etc.) is permitted
                    .anyRequest().permitAll()
            )

            // Register JWT filter before the standard username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder bean — used by {@code AuthService} (Task 5) to hash
     * and verify passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
