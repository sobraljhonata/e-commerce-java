package com.platform.iam.application;

import java.util.List;
import java.util.UUID;

/**
 * Identidade do usuário autenticado na borda HTTP (claims do JWT já validado). Não é entidade de
 * domínio nem agregado persistido.
 *
 * <p><strong>Papéis ({@code roles}):</strong> espelham a claim {@code roles} do JWT emitido pelo
 * IAM — essa claim é a <strong>fonte de verdade</strong> para este modelo. As {@code
 * GrantedAuthority} derivadas pelo Spring servem à autorização HTTP; não duplicar regras assumindo
 * equivalência sem revisar o emissor do token.
 *
 * <p><strong>Débito técnico ({@code userId}):</strong> o sujeito {@code sub} do token é hoje
 * interpretado como {@link UUID}, alinhado ao {@code JwtAccessTokenIssuer}. Se o emissor passar a
 * usar {@code sub} opaco ou não-UUID (ex.: OIDC externo), evoluir para {@code String} no modelo e
 * converter para UUID apenas onde o domínio exigir identificador tipado.
 *
 * <p>tenantId é derivado exclusivamente da claim {@code tenantId} no JWT.
 *
 * <p>Durante requisições autenticadas, o JWT é a única fonte de verdade.
 *
 * <p>O valor do AdminUser é usado apenas no momento do login para preencher o token.
 *
 * @param userId identificador do sujeito no token (atualmente {@code sub} como UUID)
 * @param tenantId claim obrigatória {@code tenantId} no JWT (contexto atual; fonte de verdade no
 *     token)
 * @param email claim obrigatória {@code email} no JWT
 * @param roles lista alinhada à claim {@code roles} do JWT (fonte de verdade para introspecção)
 */
public record AuthenticatedUser(UUID userId, UUID tenantId, String email, List<String> roles) {}
