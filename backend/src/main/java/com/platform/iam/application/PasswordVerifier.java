package com.platform.iam.application;

/** Porta: verificação de senha (implementação com BCrypt no adapter). */
public interface PasswordVerifier {

    boolean matches(String rawPassword, String encodedHash);
}
