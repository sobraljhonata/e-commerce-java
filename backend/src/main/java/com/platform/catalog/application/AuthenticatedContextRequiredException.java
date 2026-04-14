package com.platform.catalog.application;

/**
 * Operação exige usuário autenticado com contexto de tenant no token; não encontrado na borda.
 * Mapeado como HTTP 401.
 */
public final class AuthenticatedContextRequiredException extends RuntimeException {

    public AuthenticatedContextRequiredException() {
        super("Authenticated user context with tenant is required");
    }
}
