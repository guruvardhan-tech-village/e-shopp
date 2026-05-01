package com.business.expensetracker.security;

import com.business.expensetracker.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Provides JWT generation, validation, and claim extraction.
 * Uses JJWT 0.12.x API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    // -------------------------------------------------------------------------
    // Key helper
    // -------------------------------------------------------------------------

    private SecretKey signingKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Generates a signed JWT for the given user ID.
     *
     * @param userId the authenticated user's database ID
     * @return compact JWT string
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtConfig.getExpiryMs());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(jwtConfig.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    /**
     * Validates a JWT token by checking signature, expiry, and issuer.
     *
     * @param token compact JWT string
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            // Verify issuer matches expected value
            return jwtConfig.getIssuer().equals(claims.getIssuer());
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage());
        } catch (SignatureException e) {
            log.debug("JWT signature validation failed: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("JWT token is malformed: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.debug("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts the user ID (subject) from a valid JWT token.
     *
     * @param token compact JWT string (must be valid)
     * @return the user ID stored as the subject claim
     */
    public Long getUserIdFromToken(String token) {
        String subject = parseClaims(token).getSubject();
        return Long.parseLong(subject);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
