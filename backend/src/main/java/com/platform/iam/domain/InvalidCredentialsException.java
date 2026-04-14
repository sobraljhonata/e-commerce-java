package com.platform.iam.domain;

/** Falha de autenticação (credenciais inválidas ou usuário inativo). */
public final class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
