package com.business.expensetracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtConfig {

    /** Base64-encoded secret key (or plain text; JJWT 0.12.x handles both). */
    private String secret;

    /** Expected issuer claim value. */
    private String issuer;

    /** Token expiry in milliseconds. */
    private long expiryMs;
}
