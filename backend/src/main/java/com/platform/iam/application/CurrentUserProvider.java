package com.platform.iam.application;

import java.util.Optional;

/**
 * Porta: acesso ao usuário atualmente autenticado (resolvido na borda a partir do contexto HTTP).
 */
public interface CurrentUserProvider {

  Optional<AuthenticatedUser> currentUser();
}
