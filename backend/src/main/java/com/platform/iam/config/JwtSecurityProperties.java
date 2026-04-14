package com.platform.iam.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Segredo e TTL do JWT (emissão + validação na borda). Centralizado para alinhar issuer e resource
 * server.
 */
@Validated
@ConfigurationProperties(prefix = "platform.security.jwt")
public class JwtSecurityProperties {

    /** HS256: mínimo 256 bits (32 caracteres UTF-8). */
    @NotBlank
    @Size(min = 32)
    private String secret;

    @Positive private long expiresInSeconds = 3600L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }
}
