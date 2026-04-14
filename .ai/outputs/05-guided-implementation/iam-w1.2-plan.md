# Plano W1.2 — BC Identity & Access (IAM baseline)

**Papel:** planejamento executável no mesmo nível de detalhe do BC Tenant (W1.1).  
**Base:** Phase 3 (`01-bounded-contexts-oficiais.md`, stack hexagonal), fechamento `tenant-w1.1-closeout.md`, agentes Phase 4 (implementação guiada).  
**Premissas:** monólito modular, multi-tenant como capability central, **sem** reescrever o BC Tenant — **estender** a plataforma com IAM mínimo.

**Legenda**

| Tag | Uso |
|-----|-----|
| **Decisão** | Compromisso proposto para W1.2. |
| **Trade-off** | Custo vs benefício. |
| **Recomendação** | Opcional ou condicional. |

---

## 1. Objetivo da W1.2

### Papel do IAM na plataforma

**Decisão:** O BC **Identity & Access** (BC02 na Phase 3) fornece **quem** pode fazer **o quê**, com identidade estável e prova de autenticidade nas chamadas HTTP. Na W1.2 isso significa **baseline**: login, emissão de credencial de sessão (token), e **proteção** das rotas já existentes em `/api/admin/**`.

### Por que vem após Tenant

**Fato (arquitetura):** O mapa de contextos posiciona **IAM** junto à plataforma e alimenta o fluxo para **Tenant & Config** (`IAM --> TM` no diagrama oficial).

**Observação:** Tecnicamente o Tenant foi implementável sem IAM usando “admin aberto”; isso foi débito explícito na W1.1. **Sem IAM**, não há caminho seguro para **evoluir** multi-tenant (operadores por tenant, auditoria, segregação de acesso).

### Impacto nos próximos BCs

| Impacto | Descrição |
|---------|-----------|
| **Padrão** | Todo BC com API administrativa reutiliza o mesmo esquema de **autenticação/autorização** (filtro + roles). |
| **Tenant context** | Futuros use cases podem exigir `tenantId` no token ou membership — W1.2 prepara o modelo sem fechar todos os casos. |
| **Catálogo / Pedido** | Compradores (B2C) terão fluxo IAM separado depois; W1.2 foca **operador/plataforma**. |

---

## 2. Escopo funcional mínimo (IAM baseline)

| Área | **Decisão** (W1.2) |
|------|---------------------|
| **Autenticação básica** | **Login** com identificador + senha (ex.: email + password), resposta com **JWT** assinado localmente. |
| **Modelo de usuário administrativo** | Entidade **User** (ou `PlatformUser`) com id, email único, hash de senha, estado ativo; **papéis** mínimos. |
| **Associação com tenant** | **Mínimo viável:** suporte a papel **`PLATFORM_ADMIN`** (acesso global admin) **sem** membership por tenant na primeira entrega **ou** um único vínculo opcional `tenantId` no token para testes — **escolha documentada na implementação** (ver secção 10). |
| **Autorização inicial** | **RBAC de duas faixas:** `ROLE_PLATFORM_ADMIN` (protege `/api/admin/**`) e reserva para `ROLE_TENANT_ADMIN` (pode ficar sem uso até haver dados de membership). |
| **Proteção de endpoints admin** | **Spring Security** com regra: `/api/admin/**` exige autenticação + role adequada; `/api/auth/**` (ou equivalente) **público** para login. |

**Trade-off:** JWT sem refresh elaborado reduz superfície; renovação de sessão pode ser “login de novo” na Wave 1.

---

## 3. O que NÃO entra agora

- **SSO** corporativo, **SAML**, **LDAP**, **Keycloak** como IdP externo (avaliar na W2+).  
- **Múltiplos providers** sociais (Google, Microsoft, etc.).  
- **OAuth2 Authorization Server** completo (fluxos authorization code para terceiros).  
- **RBAC fino** (permissões por recurso/ação em centenas de entradas).  
- **Gestão de usuários** completa (CRUD admin de usuários, reset por e-mail, convites).  
- **MFA / passkeys**.  
- **Refresh token** com rotação e família de tokens.  
- **Auditoria de segurança** avançada (fora de logs básicos).  

**Observação:** Itens acima são **compatíveis** com a arquitetura proposta se JWT + claims forem evoluídos sem quebrar contratos da W1.2.

---

## 4. Backlog da W1.2

**Épico E-IAM-1 — Baseline de identidade e acesso (plataforma)**

| ID | Feature | História de usuário | Tasks (exemplos) | Critérios de aceitação | Dependências |
|----|---------|----------------------|------------------|------------------------|--------------|
| F1 | Modelo de usuário | Como **plataforma**, quero **representar um usuário admin** para autenticar operadores. | Agregado `User`; hash BCrypt; repositório porta | Usuário persistido em memória ou DB conforme escolha W1.2b; email único | — |
| F2 | Login | Como **operador**, quero **fazer login** para obter um token. | `AuthenticateUserUseCase`; endpoint `POST /api/auth/login`; JWT service | 200 + body com `accessToken` (e tipo); 401 credenciais inválidas | F1 |
| F3 | Segurança HTTP | Como **plataforma**, quero **proteger `/api/admin/**`**. | `SecurityFilterChain`; converter JWT → `Authentication`; testes | Sem token → 401; token válido + role → 200 nos endpoints Tenant | F2 |
| F4 | Seed / bootstrap | Como **dev**, quero **usuário inicial** documentado. | `data.sql` ou runner in-memory; credenciais só em dev | Login funciona em ambiente local conforme README | F1, F2 |
| F5 | BDD IAM | Como **time**, quero **cenários executáveis** de login e acesso. | Feature Cucumber; steps com MockMvc + Security | Cenários verdes em CI | F2, F3 |
| F6 | ArchUnit IAM | Como **arquitetura**, quero **regras de BC** para `iam`. | Testes pacote `com.platform.iam` | Regras verdes | F1–F3 |

**Dependências externas:** nenhuma obrigatória além de Spring Security + biblioteca JWT (ex.: Nimbus JOSE + JWT ou `jjwt`).

---

## 5. Cenários BDD (rascunho Gherkin)

**Idioma:** `pt` (alinhado ao Tenant).

```gherkin
# language: pt
Funcionalidade: Autenticação administrativa (W1.2)
  Cenário: Login com sucesso
    Dado que existe um usuário administrativo válido
    Quando o operador envia credenciais corretas
    Então a API responde 200
    E o corpo contém um token de acesso

  Cenário: Login inválido
    Dado que existe um usuário administrativo válido
    Quando o operador envia senha incorreta
    Então a API responde 401

  Cenário: Acesso a recurso admin sem autenticação
    Dado que o operador não está autenticado
    Quando solicita um endpoint em /api/admin/
    Então a API responde 401

  Cenário: Acesso a recurso admin com token válido
    Dado que o operador obteve um token válido
    Quando solicita GET /api/admin/tenants com o token
    Então a API responde 200
```

**Recomendação:** Estender com cenário **403** se houver role insuficiente na mesma wave.

---

## 6. Sequência TDD (sugerida)

| # | Primeiro teste | Comportamento esperado | Mínimo | Refatoração |
|---|----------------|------------------------|--------|-------------|
| 1 | Domínio: validação de senha no agregado ou serviço | Falha com credencial errada antes de IO | `PasswordHasher` fake retorna bool | Extrair interface `PasswordHasher` |
| 2 | `AuthenticateUserUseCaseTest` | Credenciais corretas → token string opaco ou JWT gerado por porta | Fake `UserRepository` + fake `TokenIssuer` | JWT real na implementação |
| 3 | `LoginControllerWebTest` | POST login → 200 + JSON | Controller + handler 401 | Mover para `ApiErrorResponse` compartilhado se duplicar |
| 4 | `SecurityIT` ou `@WebMvcTest` com Security | GET `/api/admin/tenants` sem header → 401 | `SecurityFilterChain` permitindo só `/api/auth/**` anônimo | Ajustar ordem de filtros |
| 5 | BDD feature | Mesmos fluxos em linguagem de negócio | Glue reutilizando helpers | — |

**Trade-off:** Testar Security com `@WebMvcTest` + `@Import(SecurityConfig)` vs teste de slice completo — preferir o menor que prove 401/200.

---

## 7. Estrutura de pastas do BC IAM

Alinhado ao Tenant (`com.platform.tenant` → `com.platform.iam`):

```
com.platform.iam/
  domain/
    User.java
    UserId.java                    # opcional: record UUID
    Role.java                      # enum ou conjunto fixo
    InvalidCredentialsException.java
    UserInactiveException.java
  application/
    AuthenticateUserUseCase.java
    UserRepository.java            # porta
    TokenIssuerPort.java           # emissão JWT (porta)
    PasswordHasherPort.java        # verificação BCrypt (porta)
  adapters/
    in/web/
      AuthController.java          # POST /api/auth/login
      LoginRequest.java
      LoginResponse.java
      IamExceptionHandler.java     # ou shared com plataforma
    out/persistence/
      InMemoryUserRepository.java
    out/security/
      JwtTokenIssuer.java          # implementa TokenIssuerPort
      BCryptPasswordHasher.java
  config/
    SecurityConfig.java            # se preferir fora de adapter — ou em adapters/in/web
```

**Testes:**

```
src/test/java/com/platform/iam/...
src/test/java/com/platform/bdd/iam/...
src/test/resources/features/bdd/iam_w12.feature
```

**Observação:** `SecurityConfig` pode ficar em `com.platform.iam.config` ou `com.platform.shared.security` — **decisão:** manter em **iam** na W1.2 para isolar; extrair para `shared` quando houver segundo BC consumidor.

---

## 8. Modelo inicial de classes

| Elemento | Papel |
|----------|--------|
| **User** | Agregado: id, email, passwordHash, roles, active. |
| **Role** | Enum `PLATFORM_ADMIN`, `TENANT_ADMIN` (reservado). |
| **AuthenticateUserUseCase** | `execute(email, password)` → `AuthResult` (token + expiração opcional) ou lança exceção de domínio. |
| **UserRepository** | `findByEmail`, `save` (para seed). |
| **TokenIssuerPort** | Gera string JWT com claims: `sub`, `roles`, opcional `tenant_id`. |
| **PasswordHasherPort** | `matches(raw, hash)`. |
| **AuthController** | Mapeia HTTP; não contém regra de negócio. |

**Recomendação:** Não expor entidade `User` no JSON de login — apenas token + metadata mínima (`tokenType`, `expiresIn`).

---

## 9. Estratégia de autenticação

**Decisão:** **JWT assinado (HS256 ou RS256)** emitido pelo próprio monólito após validação de credenciais.

| Opção | Prós | Contras |
|-------|------|--------|
| **JWT HS256** (segredo compartilhado) | Simples, uma chave em config | Rotação de chave manual; todos os nós precisam do segredo |
| **JWT RS256** | Melhor para múltiplos consumidores depois | Mais setup (par de chaves) na W1.2 |

**Trade-off Wave 1:** **HS256 + segredo em variável de ambiente** (`JWT_SECRET`), tempo de vida curto (ex.: 1h), **sem refresh token**.

**Observação:** Isso está alinhado à Phase 3 (OIDC “futuro”); W1.2 entrega **substituto interno** até integrar IdP.

---

## 10. Integração com Tenant

**Decisão recomendada para W1.2:**

| Relação | Direção |
|---------|---------|
| **IAM não depende do domínio Tenant** | **Fato desejável:** autenticação é transversal; `User` não precisa de FK `Tenant` para existir. |
| **Tenant não depende de IAM** | APIs de tenant continuam **sem** regra de usuário no domínio do agregado `Tenant`. |
| **Integração na borda** | **Spring Security** exige token **antes** de chegar ao `TenantController`; o BC Tenant **não** importa classes de `iam`. |

**Associação tenant (usuário):**

- **Opção A (mínima):** só `PLATFORM_ADMIN`; qualquer tenant acessível — aceitável para demo interna.  
- **Opção B:** tabela `user_tenant_membership` (ou in-memory) com `(userId, tenantId, role)` — habilita `TENANT_ADMIN` **sem** misturar agregados entre pacotes (use **IDs** e validação na camada de aplicação ou filtro).

**Recomendação:** W1.2 = **Opção A** + claim opcional `tenant_id` preparado no JWT **vazio** ou com valor de teste; membership explícita → **W1.3** ou incremento dedicado.

---

## 11. Endpoints iniciais

| Método | Caminho | Público? | Objetivo |
|--------|---------|----------|----------|
| `POST` | `/api/auth/login` | Sim | Credenciais → JWT |
| `GET` | `/api/auth/me` | Não (opcional W1.2) | Introspecção mínima do usuário autenticado |
| *existentes* | `/api/admin/tenants/**` | **Não** após W1.2 | Protegidos com Bearer JWT |

**Decisão:** Prefixo `/api/auth/**` **permitAll**; `/api/admin/**` **authenticated + hasRole('PLATFORM_ADMIN')`** (ajustar constante Spring).

**Observação:** Health/actuator podem permanecer conforme configuração global do projeto (`application.yml`).

---

## 12. Estratégia de testes

| Camada | Foco |
|--------|------|
| **Unitário** | `AuthenticateUserUseCase`, hash, expiração de claims (se testável). |
| **Integração** | `SecurityFilterChain` + um controller Tenant smoke com JWT. |
| **BDD** | Feature `iam_w12` + steps (padrão Cucumber do projeto). |
| **Segurança básica** | Testes que **não** aceitam token adulterado; ausência de credenciais → 401. |

**Recomendação:** Não usar **somente** mocks do `Authentication` nos testes de Tenant — pelo menos **um** teste e2e leve com JWT gerado pelo `TokenIssuer` real.

---

## 13. Regras arquiteturais novas (ArchUnit)

Sugestões de regras (além das existentes):

1. **`noClasses().that().resideInAPackage("..iam..").should().dependOnClassesThat().resideInAPackage("..tenant.domain..")`** — IAM não acopla ao domínio Tenant (aplicação pode referenciar **porta** `TenantRepository` só se houver caso de uso explícito; **evitar** na W1.2).  
2. **`noClasses().that().resideInAPackage("..tenant..").should().dependOnClassesThat().resideInAPackage("..iam.domain..")`** — Tenant não importa domínio IAM.  
3. **Adapters de segurança** (`..iam.adapters.out.security..`) só acessíveis por `iam` + `config` + testes.  
4. **Domínio IAM** sem dependência de Spring (`..iam.domain..` não depende de `org.springframework..`).

**Observação:** Ajustar `AdapterAccessRulesTest` para incluir pacote `..iam..` na lista de consumidores permitidos de adapters, espelhando `..tenant..`.

---

## 14. Critérios de pronto da W1.2

- [ ] Login funcional com resposta documentada (200/401).  
- [ ] `/api/admin/**` retorna **401** sem token e **200/2xx** com JWT válido nos fluxos já cobertos pelo Tenant.  
- [ ] Testes unitários + integração segurança + BDD verdes.  
- [ ] ArchUnit atualizado e **verde**.  
- [ ] README ou doc curta: como obter token e chamar admin API.  
- [ ] Segredo JWT **não** commitado (uso de env / placeholder).

---

## 15. Plano de implementação (cronológico)

| Ordem | Incremento | Entrega | Validação |
|-------|------------|---------|-----------|
| I1 | Domínio `User` + exceções + ports | Testes unitários domínio | ArchUnit domínio |
| I2 | `InMemoryUserRepository` + seed + `BCryptPasswordHasher` | CRUD mínimo interno ou só seed | — |
| I3 | `JwtTokenIssuer` + `AuthenticateUserUseCase` | Use case testado | — |
| I4 | `AuthController` + DTOs + handler 401 | WebMvc test | — |
| I5 | `SecurityConfig` + proteção `/api/admin/**` | Teste 401/200 em Tenant | Smoke manual opcional |
| I6 | Feature BDD + glue | Cucumber verde | — |
| I7 | ArchUnit IAM + ajuste regras cross-BC | CI verde | — |
| I8 | Documentação + fechamento W1.2 | Artefato tipo `iam-w1.2-closeout.md` | Review |

**Commits sugeridos (granularidade):** um commit por incremento I1–I7; I8 separado.

**Pontos de validação:** após **I5** (segurança), rodar suite completa do Tenant + novo IAM; após **I7**, pipeline CI.

---

## Síntese de trade-offs

| Tema | Escolha W1.2 |
|------|----------------|
| Token | JWT local, curto, sem refresh |
| Usuários | Mínimo; seed para dev |
| Multi-tenant admin | Deferir membership fino |
| Integração Tenant | Só na borda (Security), não no domínio |

---

**Próximo passo recomendado:** após aprovação deste plano, criar **incrementos numerados** (como Tenant W1.1) e rastrear no mesmo diretório `.ai/outputs/05-guided-implementation/`.
