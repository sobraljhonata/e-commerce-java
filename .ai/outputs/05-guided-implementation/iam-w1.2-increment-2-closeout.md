# IAM W1.2 — Micro fechamento do Incremento 2 (segurança HTTP + JWT)

**Tipo:** fechamento arquitetural (análise e registro; sem alteração de código neste artefato).  
**Base:** implementação em `backend` (Incremento 2), documento `iam-w1.2-incremento2-security-jwt.md`, revisão crítica pós-implementação.

---

## 1. Objetivo do incremento

Proteger **`/api/admin/**`** com **Bearer JWT** validado na borda HTTP, mantendo **`POST /api/auth/login`** público, e **centralizar segredo e TTL** do JWT para que **emissão** (incremento anterior) e **validação** (resource server) usem a **mesma configuração**.

---

## 2. O que foi entregue

| Área | Entrega |
|------|---------|
| **Spring Security** | Dependências mínimas (`security`, `oauth2-resource-server`); **`SecurityFilterChain` explícito** em `IamSecurityConfiguration`. |
| **Rotas** | `POST /api/auth/login` → `permitAll`; `/api/admin/**` → `hasRole("PLATFORM_ADMIN")`; actuator e Swagger documentados como públicos; `anyRequest()` → `permitAll` (Wave 1). |
| **JWT** | `JwtDecoder` (Nimbus, HS256) + `JwtAuthenticationConverter` (claim `roles` → `ROLE_*`); alinhado ao `JwtAccessTokenIssuer` (JJWT). |
| **Config** | `JwtSecurityProperties` (`platform.security.jwt.*`) injetada no **issuer** e no **decoder**. |
| **Bootstrap** | `Wave1BackendApplication` com exclusão de JPA/DataSource enquanto persistência for só em memória. |
| **Testes** | `AdminApiSecurityIntegrationTest` (401 / 200 / 404 / token inválido); BDD `IamAdminAccessW12BddSuite` + feature `iam_admin_access_w12.feature`. |
| **Documentação de implementação** | `iam-w1.2-incremento2-security-jwt.md` (como construir/validar manualmente). |

---

## 3. Decisões arquiteturais confirmadas

| Decisão | Status |
|---------|--------|
| **Hexagonal:** domínio e casos de uso IAM **sem** dependência de Spring Security | **Confirmada** — segurança só em `com.platform.iam.config` + adaptadores. |
| **Borda única para HTTP auth** | **Confirmada** — `SecurityFilterChain` + OAuth2 Resource Server (`JwtDecoder`). |
| **Segredo e TTL únicos** | **Confirmada** — `JwtSecurityProperties` alimenta **emissor** e **decoder**; expiração de sessão via claim `exp` + TTL de emissão. |
| **Papel mínimo** | **Confirmada** — `PLATFORM_ADMIN` para admin; sem refresh, sem OAuth externo, sem tenant no token neste incremento. |
| **Pragmatismo Wave 1** | **Confirmada** — `anyRequest().permitAll()` até surgirem mais superfícies `/api/**` definidas. |

---

## 4. Débitos conscientes

| Débito | Natureza |
|--------|----------|
| **`anyRequest().permitAll()`** | **Débito de política de superfície:** novas rotas fora de `/api/admin/**` ficam abertas por omissão até regra explícita. |
| **Equivalência HS256 issuer/decoder** | **Débito de evidência:** compatível na prática; não há teste dedicado que nomeie “round-trip” como contrato formal. |
| **CORS** | **Débito operacional:** não tratado; relevante quando houver SPA em outro origin. |
| **Corpo de erro 401/403** | **Débito de UX/API:** respostas padrão do resource server; eventual alinhamento com `IamApiErrorResponse` é trabalho de borda. |
| **Multi-tenant / escopo** | **Débito de produto:** token não carrega `tenant_id` nem membership; admin global não está restrito por tenant — esperado para W1.2, insuficiente para produção multi-tenant. |
| **Actuator público** | **Débito de hardening:** `/actuator/**` permitido na config; revisão por ambiente (métricas, health) recomendada antes de produção. |

---

## 5. Ajustes imediatos de robustez (recomendações)

> **Recomendações**, não exigências do incremento; implementação em histórias subsequentes.

1. **Documentar** variáveis `JWT_SECRET` e `JWT_EXPIRES_SECONDS` e proibir default fraco em produção (ver tasks abaixo).
2. **Teste explícito round-trip** issuer → decoder → `GET /api/admin/**` como contrato de regressão (ver tasks).
3. **Registrar em ADR ou wiki** a decisão temporária de `anyRequest().permitAll()` e o gatilho para endurecer (ex.: primeira rota `/api` não pública).
4. **Revisar actuator** por ambiente antes de exposição pública de métricas.

---

## 6. Critério de pronto atingido

| Critério (Incremento 2) | Estado |
|-------------------------|--------|
| Spring Security mínimo + `SecurityFilterChain` explícito | **Atendido** |
| `/api/auth/login` aberto; `/api/admin/**` protegido | **Atendido** |
| Validação do JWT emitido previamente (mesmo segredo / HS256) | **Atendido** (com ressalva de teste nomeado round-trip — débito leve) |
| Config centralizada (`platform.security.jwt`) | **Atendido** |
| Testes web + BDD mínimos para rota protegida | **Atendido** |

**Conclusão:** o incremento cumpre o **escopo Wave 1** acordado; **não** equivale a “pronto para produção multi-tenant” (ver §4).

---

## 7. Próximos passos (linha do IAM / plataforma)

1. **W1.2 restante ou W1.3:** refresh opcional, ou documentar “re-login” como política até nova wave.
2. **Tenant no token + filtro de tenant** quando o BC Tenant exigir segregação por operador.
3. **Endurecimento:** política default para `anyRequest`, CORS, handlers de 401/403 alinhados ao contrato de erro da plataforma.
4. **ArchUnit / pacotes:** manter regra “tenant não depende de iam.domain”; revisar se `iam.config` permanece só no BC IAM.

---

## Tasks curtas de fechamento (backlog)

| ID | Task | Tipo |
|----|------|------|
| **T-IAM-CLOSE-01** | **Documentação da configuração JWT:** descrever `platform.security.jwt.secret`, `expires-in-seconds`, env `JWT_SECRET` / `JWT_EXPIRES_SECONDS`, requisito mínimo de 32 caracteres, e aviso de não usar default em produção. Local sugerido: README do `backend` ou seção no `iam-w1.2-plan.md`. | Documentação |
| **T-IAM-CLOSE-02** | **Teste explícito round-trip issuer/decoder:** teste de integração (ou nomeação + comentário) que documente o contrato “token emitido por `JwtAccessTokenIssuer` é aceito pelo `JwtDecoder` bean em `GET /api/admin/**`”. | Teste / qualidade |
| **T-IAM-CLOSE-03** | **Registro da decisão temporária `anyRequest().permitAll()`:** ADR curto ou nota no repositório `.ai` com: contexto (Wave 1), risco (superfície aberta), condição de revisão (novas rotas `/api` ou hardening). | Decisão arquitetural |

---

*Arquiteto de referência: decisões de produto multi-tenant e produção permanecem fora do escopo deste fechamento; §4 e §7 delimitam fato, débito e recomendação.*
