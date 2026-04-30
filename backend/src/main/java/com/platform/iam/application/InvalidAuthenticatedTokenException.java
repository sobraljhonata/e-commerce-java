package com.platform.iam.application;

/**
 * Token JWT autenticado pelo resource server, porém com claims insuficientes ou inválidas para
 * montar {@link AuthenticatedUser}. Tratado na borda como 401 (credencial/autenticação não
 * utilizável).
 */
public final class InvalidAuthenticatedTokenException extends RuntimeException {

  public InvalidAuthenticatedTokenException(String message) {
    super(message);
  }
}
