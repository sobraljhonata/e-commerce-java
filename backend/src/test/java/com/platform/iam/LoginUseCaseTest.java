package com.platform.iam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import com.platform.iam.adapters.out.security.BCryptPasswordVerifier;
import com.platform.iam.adapters.out.security.JwtAccessTokenIssuer;
import com.platform.iam.application.LoginUseCase;
import com.platform.iam.domain.AdminRole;
import com.platform.iam.domain.AdminUser;
import com.platform.iam.domain.InvalidCredentialsException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class LoginUseCaseTest {

    private static final String JWT_SECRET = "test-jwt-secret-key-at-least-32-bytes-long";

    @Test
    void should_return_token_when_credentials_valid() {
        var repo = new InMemoryAdminUserRepository();
        var useCase =
            new LoginUseCase(
                repo, new BCryptPasswordVerifier(), new JwtAccessTokenIssuer(JWT_SECRET, 3600));

        LoginUseCase.LoginResult result =
            useCase.execute(InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);

        assertNotNull(result.accessToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(3600L, result.expiresIn());
    }

    @Test
    void should_fail_when_password_wrong() {
        var repo = new InMemoryAdminUserRepository();
        var useCase =
            new LoginUseCase(
                repo, new BCryptPasswordVerifier(), new JwtAccessTokenIssuer(JWT_SECRET, 3600));

        assertThrows(
            InvalidCredentialsException.class,
            () -> useCase.execute(InMemoryAdminUserRepository.SEED_EMAIL, "wrong-password"));
    }

    @Test
    void should_fail_when_user_unknown() {
        var repo = InMemoryAdminUserRepository.empty();
        var useCase =
            new LoginUseCase(
                repo, new BCryptPasswordVerifier(), new JwtAccessTokenIssuer(JWT_SECRET, 3600));

        assertThrows(
            InvalidCredentialsException.class, () -> useCase.execute("nobody@local.dev", "any"));
    }

    @Test
    void should_fail_when_user_inactive() {
        var repo = InMemoryAdminUserRepository.empty();
        var encoder = new BCryptPasswordEncoder();
        repo.save(
            AdminUser.restore(
                UUID.randomUUID(),
                UUID.fromString("99999999-9999-9999-9999-999999999999"),
                "inactive@local.dev",
                encoder.encode("secret"),
                false,
                AdminRole.PLATFORM_ADMIN));

        var useCase =
            new LoginUseCase(
                repo, new BCryptPasswordVerifier(), new JwtAccessTokenIssuer(JWT_SECRET, 3600));

        assertThrows(
            InvalidCredentialsException.class, () -> useCase.execute("inactive@local.dev", "secret"));
    }
}
