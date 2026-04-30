# W1.2 — Incremento 2: Spring Security + JWT em `/api/admin/**`

## 1. Objetivo do incremento

Proteger endpoints administrativos (`/api/admin/**`) com validação de **Bearer JWT** (mesmo segredo e HS256 do emissor `JwtAccessTokenIssuer`), mantendo **`POST /api/auth/login`** público e alinhando **TTL/segredo** via configuração centralizada.

## 2. Decisão arquitetural

| Decisão | Motivo |
|---------|--------|
| **`SecurityFilterChain` explícito** em `com.platform.iam.config.IamSecurityConfiguration` | Borda HTTP; domínio/aplicação IAM sem imports de Spring Security |
| **OAuth2 Resource Server + `JwtDecoder` (Nimbus)** | Validação padrão de JWT; mesma chave HMAC que o issuer JJWT |
| **`JwtGrantedAuthoritiesConverter`** com claim `roles` → `ROLE_*` | Compatível com `hasRole("PLATFORM_ADMIN")` |
| **`JwtSecurityProperties`** (`platform.security.jwt`) | Um único ponto para `secret` e `expires-in-seconds` usados pelo **issuer** e pelo **decoder** |
| **`Wave1BackendApplication` exclui JPA/DataSource** | Repositórios W1 ainda em memória; JPA no classpath sem config local |

## 3. Sequência TDD sugerida (executada)

1. Falha esperada: GET admin sem token → 401 (teste de integração).
2. Configurar `SecurityFilterChain` + `JwtDecoder` → verde.
3. Login continua 200; admin com token → não-401 (404 tenant inexistente).
4. BDD: sem token 401; com token 404.

## 4. Implementação mínima

- Dependências: `spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server`.
- Beans IAM explícitos: `IamBeansConfiguration` (já existente padrão).
- `application.yml`: `platform.security.jwt.secret`, `expires-in-seconds` (+ env `JWT_SECRET`, `JWT_EXPIRES_SECONDS`).

## 5. Arquivos criados/alterados

| Arquivo | Ação |
|---------|------|
| `backend/pom.xml` | +security, +oauth2-resource-server, +cucumber-spring (test) |
| `backend/src/main/java/com/platform/Wave1BackendApplication.java` | **Novo** — `@SpringBootApplication`, exclui JPA/DS |
| `backend/src/main/java/com/platform/iam/config/JwtSecurityProperties.java` | **Novo** |
| `backend/src/main/java/com/platform/iam/config/IamBeansConfiguration.java` | **Novo** |
| `backend/src/main/java/com/platform/iam/config/IamSecurityConfiguration.java` | **Novo** |
| `backend/src/main/java/com/platform/tenant/config/TenantBeansConfiguration.java` | **Novo** |
| `backend/src/main/resources/application.yml` | Propriedades JWT |
| `backend/src/test/java/com/platform/iam/adapters/in/web/AdminApiSecurityIntegrationTest.java` | **Novo** |
| `backend/src/test/java/com/platform/bdd/iam/api/CucumberSpringBootConfiguration.java` | **Novo** |
| `backend/src/test/java/com/platform/bdd/iam/api/AdminApiStepDefinitions.java` | **Novo** |
| `backend/src/test/java/com/platform/bdd/iam/IamAdminAccessW12BddSuite.java` | **Novo** |
| `backend/src/test/resources/features/bdd/iam/iam_admin_access_w12.feature` | **Novo** |

## 6. Testes web e BDD

- **Integração:** `AdminApiSecurityIntegrationTest` — 401 sem token; login 200; admin com JWT → 404 conhecido; Bearer inválido → 401.
- **BDD:** `IamAdminAccessW12BddSuite` + `iam_admin_access_w12.feature` (2 cenários; steps sem `/` literal para evitar parser Cucumber).

## 7. Validação manual

```bash
cd backend && mvn -q test
# Subir app (quando usar spring-boot:run):
export JWT_SECRET="local-dev-jwt-secret-change-me-32b-min!!"
mvn spring-boot:run
```

- `POST http://localhost:8080/api/auth/login` com `{"email":"admin@local.dev","password":"admin-secret"}` → 200 + `accessToken`.
- `GET http://localhost:8080/api/admin/tenants/slug/x` sem header → **401**.
- Mesmo GET com `Authorization: Bearer <accessToken>` → **404** `TENANT_NOT_FOUND` (esperado).

## 8. Observações de arquitetura

- **Hexagonal:** regras de autenticação de credenciais permanecem no `LoginUseCase`; Spring Security só valida JWT na borda.
- **Wave 1:** apenas `ROLE_PLATFORM_ADMIN` para `/api/admin/**`; sem refresh. Na evolução W1.2, `tenantId` foi incorporado ao JWT/contexto autenticado para consumo dos BCs tenant-scoped.
- **Cucumber:** glue em `com.platform.bdd.iam.api` sem `@Component` nas step definitions (exigência cucumber-spring).
