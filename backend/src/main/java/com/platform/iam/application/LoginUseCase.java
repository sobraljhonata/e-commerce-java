package com.platform.iam.application;

import com.platform.iam.domain.AdminUser;
import com.platform.iam.domain.InvalidCredentialsException;

public final class LoginUseCase {

    private final AdminUserRepository users;
    private final PasswordVerifier passwordVerifier;
    private final AccessTokenIssuer accessTokenIssuer;

    public LoginUseCase(
            AdminUserRepository users,
            PasswordVerifier passwordVerifier,
            AccessTokenIssuer accessTokenIssuer) {
        this.users = users;
        this.passwordVerifier = passwordVerifier;
        this.accessTokenIssuer = accessTokenIssuer;
    }

    public LoginResult execute(String email, String rawPassword) {
        String canonical = AdminUser.canonicalEmail(email);
        AdminUser user =
            users.findByEmail(canonical).orElseThrow(InvalidCredentialsException::new);
        if (!user.active()) {
            throw new InvalidCredentialsException();
        }
        if (!passwordVerifier.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        AccessTokenIssuer.IssuedAccessToken issued = accessTokenIssuer.issue(user);
        return new LoginResult(issued.value(), "Bearer", issued.expiresInSeconds());
    }

    public record LoginResult(String accessToken, String tokenType, long expiresIn) {}
}
