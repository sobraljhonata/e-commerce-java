package com.platform.iam.application;

import com.platform.iam.domain.AdminUser;

/** Porta: emissão de JWT de acesso (implementação no adapter). */
public interface AccessTokenIssuer {

    IssuedAccessToken issue(AdminUser user);

    record IssuedAccessToken(String value, long expiresInSeconds) {}
}
