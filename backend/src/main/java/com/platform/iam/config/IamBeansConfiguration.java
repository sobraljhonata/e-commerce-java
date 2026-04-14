package com.platform.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import com.platform.iam.adapters.out.security.BCryptPasswordVerifier;
import com.platform.iam.adapters.out.security.JwtAccessTokenIssuer;
import com.platform.iam.adapters.out.security.SecurityContextCurrentUserProvider;
import com.platform.iam.application.AccessTokenIssuer;
import com.platform.iam.application.AdminUserRepository;
import com.platform.iam.application.CurrentUserProvider;
import com.platform.iam.application.LoginUseCase;
import com.platform.iam.application.PasswordVerifier;

@Configuration
public class IamBeansConfiguration {

    @Bean
    AdminUserRepository adminUserRepository() {
        return new InMemoryAdminUserRepository();
    }

    @Bean
    PasswordVerifier passwordVerifier() {
        return new BCryptPasswordVerifier();
    }

    @Bean
    AccessTokenIssuer accessTokenIssuer(JwtSecurityProperties jwt) {
        return new JwtAccessTokenIssuer(jwt.getSecret(), jwt.getExpiresInSeconds());
    }

    @Bean
    LoginUseCase loginUseCase(
            AdminUserRepository users, PasswordVerifier passwords, AccessTokenIssuer tokens) {
        return new LoginUseCase(users, passwords, tokens);
    }

    @Bean
    CurrentUserProvider currentUserProvider() {
        return new SecurityContextCurrentUserProvider();
    }
}
