package com.platform.iam.adapters.out.security;

import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import com.platform.iam.application.InvalidAuthenticatedTokenException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Resolve {@link AuthenticatedUser} a partir do JWT já validado pelo resource server (borda).
 *
 * <p>Claims {@code email}, {@code tenantId} e {@code roles} vêm do JWT; ausência ou formato
 * inválido de {@code sub} ou {@code tenantId} → {@link InvalidAuthenticatedTokenException} (401 na
 * borda).
 */
public final class SecurityContextCurrentUserProvider implements CurrentUserProvider {

  @Override
  public Optional<AuthenticatedUser> currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || !(authentication instanceof JwtAuthenticationToken jwtAuth)) {
      return Optional.empty();
    }
    Jwt jwt = jwtAuth.getToken();
    return Optional.of(map(jwt));
  }

  private static AuthenticatedUser map(Jwt jwt) {
    String sub = jwt.getSubject();
    if (sub == null || sub.isBlank()) {
      throw new InvalidAuthenticatedTokenException("JWT missing subject (sub)");
    }
    UUID userId;
    try {
      userId = UUID.fromString(sub);
    } catch (IllegalArgumentException ignored) {
      throw new InvalidAuthenticatedTokenException("JWT subject (sub) is not a valid UUID");
    }
    String email = jwt.getClaimAsString("email");
    if (email == null || email.isBlank()) {
      throw new InvalidAuthenticatedTokenException("JWT missing email claim");
    }
    String tenantClaim = jwt.getClaimAsString("tenantId");
    if (tenantClaim == null || tenantClaim.isBlank()) {
      throw new InvalidAuthenticatedTokenException("JWT missing tenantId claim");
    }
    UUID tenantId;
    try {
      tenantId = UUID.fromString(tenantClaim);
    } catch (IllegalArgumentException ignored) {
      throw new InvalidAuthenticatedTokenException("JWT tenantId claim is not a valid UUID");
    }
    List<String> roles = extractRoles(jwt);
    return new AuthenticatedUser(userId, tenantId, email, roles);
  }

  private static List<String> extractRoles(Jwt jwt) {
    Object raw = jwt.getClaim("roles");
    if (raw == null) {
      return List.of();
    }
    if (raw instanceof Collection<?> c) {
      return c.stream().map(Object::toString).toList();
    }
    if (raw instanceof String s) {
      return List.of(s);
    }
    return List.of(raw.toString());
  }
}
