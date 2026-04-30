package com.platform.iam.adapters.out.security;

import com.platform.iam.application.AccessTokenIssuer;
import com.platform.iam.domain.AdminUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;

/** JWT HS256 com claims mínimos (sub, email, roles, tenantId). */
/**
 * Emite tenantId do AdminUser no JWT.
 *
 * <p>Após emissão, o JWT torna-se a fonte de verdade para o contexto do tenant.
 */
public final class JwtAccessTokenIssuer implements AccessTokenIssuer {

  private final SecretKey key;
  private final long expiresInSeconds;

  public JwtAccessTokenIssuer(String secret, long expiresInSeconds) {
    if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 256 bits");
    }
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expiresInSeconds = expiresInSeconds;
  }

  @Override
  public IssuedAccessToken issue(AdminUser user) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(expiresInSeconds);
    String jwt =
        Jwts.builder()
            .subject(user.id().toString())
            .claim("email", user.email())
            .claim("tenantId", user.tenantId().toString())
            .claim("roles", List.of(user.role().name()))
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(key)
            .compact();
    return new IssuedAccessToken(jwt, expiresInSeconds);
  }
}
