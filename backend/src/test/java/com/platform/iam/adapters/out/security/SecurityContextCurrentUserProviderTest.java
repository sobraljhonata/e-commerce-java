package com.platform.iam.adapters.out.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import com.platform.iam.application.InvalidAuthenticatedTokenException;

class SecurityContextCurrentUserProviderTest {

    private final CurrentUserProvider provider = new SecurityContextCurrentUserProvider();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void maps_jwt_claims_to_authenticated_user() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID tenantId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        Jwt jwt =
                new Jwt(
                        "token-value",
                        Instant.now(),
                        Instant.now().plusSeconds(3600),
                        Map.of("alg", "HS256"),
                        Map.of(
                                "sub",
                                id.toString(),
                                "email",
                                "seed@local.dev",
                                "tenantId",
                                tenantId.toString(),
                                "roles",
                                List.of("PLATFORM_ADMIN")));

        var auth =
                new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        var user = provider.currentUser();
        assertTrue(user.isPresent());
        AuthenticatedUser u = user.get();
        assertEquals(id, u.userId());
        assertEquals(tenantId, u.tenantId());
        assertEquals("seed@local.dev", u.email());
        assertEquals(List.of("PLATFORM_ADMIN"), u.roles());
    }

    @Test
    void empty_when_not_jwt_authentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertTrue(provider.currentUser().isEmpty());
    }

    @Test
    void throws_when_email_claim_missing() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID tenantId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        Jwt jwt =
                new Jwt(
                        "token-value",
                        Instant.now(),
                        Instant.now().plusSeconds(3600),
                        Map.of("alg", "HS256"),
                        Map.of(
                                "sub",
                                id.toString(),
                                "tenantId",
                                tenantId.toString(),
                                "roles",
                                List.of("PLATFORM_ADMIN")));

        var auth =
                new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(InvalidAuthenticatedTokenException.class, () -> provider.currentUser());
    }

    @Test
    void throws_when_sub_is_not_valid_uuid() {
        UUID tenantId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        Jwt jwt =
                new Jwt(
                        "token-value",
                        Instant.now(),
                        Instant.now().plusSeconds(3600),
                        Map.of("alg", "HS256"),
                        Map.of(
                                "sub",
                                "opaque-subject",
                                "email",
                                "a@b.c",
                                "tenantId",
                                tenantId.toString(),
                                "roles",
                                List.of("PLATFORM_ADMIN")));

        var auth =
                new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(InvalidAuthenticatedTokenException.class, () -> provider.currentUser());
    }

    @Test
    void throws_when_tenant_id_claim_missing() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Jwt jwt =
                new Jwt(
                        "token-value",
                        Instant.now(),
                        Instant.now().plusSeconds(3600),
                        Map.of("alg", "HS256"),
                        Map.of("sub", id.toString(), "email", "a@b.c", "roles", List.of("PLATFORM_ADMIN")));

        var auth =
                new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(InvalidAuthenticatedTokenException.class, () -> provider.currentUser());
    }

    @Test
    void throws_when_tenant_id_not_valid_uuid() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Jwt jwt =
                new Jwt(
                        "token-value",
                        Instant.now(),
                        Instant.now().plusSeconds(3600),
                        Map.of("alg", "HS256"),
                        Map.of(
                                "sub",
                                id.toString(),
                                "email",
                                "a@b.c",
                                "tenantId",
                                "not-a-uuid",
                                "roles",
                                List.of("PLATFORM_ADMIN")));

        var auth =
                new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(InvalidAuthenticatedTokenException.class, () -> provider.currentUser());
    }
}
