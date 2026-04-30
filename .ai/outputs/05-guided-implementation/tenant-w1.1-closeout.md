# Fechamento técnico — BC Tenant (W1.1)

> Nota de contexto (2026-04-29): este documento é um fechamento histórico da W1.1.
> O estado atual da plataforma já evoluiu com IAM W1.2 (login, JWT, proteção admin, `AuthenticatedUser`, `tenantId` no JWT/contexto) e Catalog tenant-aware.
> Use este artefato como linha de base de Tenant; para estado corrente, cruzar com os closeouts de IAM/Catalog em `.ai/outputs/05-guided-implementation/`.

**Papel:** arquitetura / referência para próximos bounded contexts.  
**Base:** código em `backend/` (pacote `com.platform.tenant`), outputs de fases anteriores e agentes Phase 4 (implementação guiada).  
**Escopo:** apenas documentação; **nenhuma alteração de código** neste artefato.

**Legenda**

| Tag | Significado |
|-----|-------------|
| **Fato** | Verificável no repositório ou em execução de testes. |
| **Observação** | Interpretação ou limite consciente. |
| **Recomendação** | Próximo passo sugerido, não obrigatório neste fechamento. |

---

## 1. Resumo da W1.1

### Objetivo do incremento

**Fato:** Entregar a **primeira fatia completa** do BC **Tenant & Platform Config** (BC01 na arquitetura Phase 3): API admin mínima, regras de slug e lifecycle (`active`), contratos de erro HTTP e testes em camadas — **sem** persistência JDBC ainda, com **in-memory** como substituto controlado.

### Capacidades entregues

**Fato:** CRUD operacional mínimo de **tenant** (criar; ler por id e por slug; alterar apenas **status** ativo/inativo); validação de **unicidade de slug** (canônica); respostas de erro alinhadas a códigos estáveis; **BDD executável** em português cobrindo os fluxos principais.

### Valor para a plataforma

**Observação:** Sem tenant estável, os demais BCs não têm âncora de isolamento. **Fato:** Este módulo define **`tenant_id`** como conceito de produto e **referência de implementação** (hexágono, exceções, borda HTTP, testes) para Catalog, Order, etc.

---

## 2. Escopo implementado

### Endpoints (**Fato** — `TenantController`, base `/api/admin/tenants`)

| Método | Caminho | Sucesso |
|--------|---------|--------|
| `POST` | `/` | 201 Created |
| `GET` | `/slug/{slug}` | 200 OK |
| `PATCH` | `/{id}/status` | 200 OK |
| `GET` | `/{id}` | 200 OK |

**Observação:** Rotas mais específicas (`/slug/{slug}`) aparecem **antes** de `/{id}` no código para evitar ambiguidade com o segmento literal `slug`.

### Use cases (**Fato** — pacote `application`)

| Classe | Responsabilidade |
|--------|------------------|
| `CreateTenantUseCase` | Criar tenant; checar duplicidade de slug canônico. |
| `GetTenantByIdUseCase` | Obter por UUID ou falhar. |
| `GetTenantBySlugUseCase` | Obter por slug (normalizado) ou falhar. |
| `UpdateTenantStatusUseCase` | Ativar/desativar via agregado; persistir. |

Porta: `TenantRepository` (save, findById, findBySlug).

### Regras de negócio (**Fato**)

- Slug **canônico** único: `Tenant.canonicalSlug` = trim + lowercase (alinhado entre criação, duplicidade e leitura).
- Criação: slug e display name obrigatórios (`Tenant.create` / validação HTTP).
- Leitura por slug vazia após normalização → `IllegalArgumentException` (400 via handler), não 404 de “recurso”.
- Ativação/desativação **idempotentes** no agregado (`activate` / `deactivate` retornam `this` se não houver mudança).

### Erros tratados na borda (**Fato** — `TenantExceptionHandler` + `ApiErrorResponse`)

| Situação | HTTP | Código no corpo (quando aplicável) |
|----------|------|-----------------------------------|
| Validação Bean Validation (`@Valid`) | 400 | `VALIDATION_ERROR` |
| `IllegalArgumentException` (ex.: slug inválido em use case) | 400 | `INVALID_REQUEST` |
| Slug duplicado | 409 | `DUPLICATE_TENANT_SLUG` + `slug` |
| Tenant não encontrado (id) | 404 | `TENANT_NOT_FOUND` + `tenantId` |
| Tenant não encontrado (slug) | 404 | `TENANT_NOT_FOUND` + `slug` |

### Comportamentos do agregado (**Fato** — `Tenant`)

- `create`: gera id, aplica slug canônico, trim de display name, **ativo = true**.
- `activate` / `deactivate`: imutabilidade com nova instância quando o estado muda; idempotência quando já no estado desejado.

---

## 3. Estrutura final do BC Tenant

### Organização de pastas (**Fato**)

```
com.platform.tenant/
  domain/
    Tenant.java
    DuplicateTenantSlugException.java
    TenantNotFoundException.java
    TenantNotFoundBySlugException.java
  application/
    TenantRepository.java
    CreateTenantUseCase.java
    GetTenantByIdUseCase.java
    GetTenantBySlugUseCase.java
    UpdateTenantStatusUseCase.java
  adapters/
    in/web/
      TenantController.java
      CreateTenantRequest.java
      UpdateTenantStatusRequest.java
      TenantResponse.java
      ApiErrorResponse.java
      TenantExceptionHandler.java
    out/persistence/
      InMemoryTenantRepository.java
```

**Observação:** Não há pacote `config` Spring dedicado ao BC no repositório atual; wiring em testes/BDD monta o controller manualmente.

### Camadas

| Camada | Papel |
|--------|--------|
| **domain** | Agregado, invariantes, exceções de negócio; sem Spring. |
| **application** | Casos de uso + porta `TenantRepository`. |
| **adapters.in.web** | REST, DTOs de entrada/saída, mapeamento HTTP. |
| **adapters.out.persistence** | Implementação volátil do repositório. |

### Principais classes (referência)

- **Domínio:** `Tenant`, exceções listadas acima.  
- **Aplicação:** quatro use cases + `TenantRepository`.  
- **Web:** `TenantController`, `TenantExceptionHandler`, `ApiErrorResponse`.  
- **Persistência:** `InMemoryTenantRepository`.

---

## 4. Padrões estabelecidos (replicar nos próximos BCs)

| Padrão | Descrição |
|--------|-----------|
| **Exceções de domínio tipadas** | **Fato:** Conflito de slug e “não encontrado” não usam mensagens genéricas como única distinção; tipos dedicados com dados (`slug()`, `tenantId()`). |
| **Tratamento HTTP na borda** | **Fato:** `@RestControllerAdvice` no adapter; controllers sem `try/catch`. |
| **Controller fino** | **Fato:** Delega a um use case; mapeamento via `toResponse(Tenant)`. |
| **`Tenant.canonicalSlug`** | **Fato:** Única função de normalização compartilhada por create e leitura por slug. |
| **Comportamento no agregado** | **Fato:** Mudança de status via `activate`/`deactivate`, não setters genéricos. |
| **DTOs de comando específicos** | **Fato:** `CreateTenantRequest`, `UpdateTenantStatusRequest` — evitar “patch genérico” na Wave 1. |

**Recomendação:** Novos BCs devem copiar **estrutura de pacotes por BC**, não “por camada global” solta.

---

## 5. Estratégia de testes

| Tipo | **Fato** (local / papel) |
|------|---------------------------|
| **Unitários** | Use cases com `InMemoryTenantRepository`; domínio (`TenantStatusTest`); asserts de idempotência em `UpdateTenantStatusUseCaseTest`. |
| **Web** | `TenantControllerWebTest` — MockMvc standalone, use cases mockados ou stack real conforme teste; `TenantExceptionHandler` registrado. |
| **BDD** | Cucumber, `features/bdd/tenant_admin_w11.feature` + `TenantAdminStepDefinitions` — stack **real** (repositório + use cases + controller + handler). |
| **Arquitetura** | `HexagonalDomainDependencyTest`, `ApplicationIndependenceTest`, `BoundedContextIsolationTest`, `AdapterAccessRulesTest` (pacote `..bdd..` permitido no acesso a adapters). |

**Observação:** Jacoco no `pom` impõe cobertura mínima em `domain` e `application` no `verify` (quando executado).

**Recomendação:** Manter BDD como **especificação executável**; evitar duplicar cenários sem valor (drift com web tests).

---

## 6. Cenários BDD existentes

**Fato:** Arquivo `tenant_admin_w11.feature` (português, tags `@tenant @w11`):

1. Criar tenant com slug único (201, slug, ativo, id válido).  
2. Rejeitar criação quando o slug já existe (409, `DUPLICATE_TENANT_SLUG`, slug).  
3. Consultar tenant por id (200, slug).  
4. Consultar tenant por slug (200, slug).  
5. Consultar tenant por id inexistente (404, `TENANT_NOT_FOUND`, `tenantId`).  
6. Desativar tenant (200, inativo).  
7. Reativar tenant (200, ativo).

**Observação:** Não há cenário explícito de **404 por slug inexistente** no feature (capacidade existe no código).

**Recomendação:** Tratar o `.feature` como **contrato vivo** entre produto, QA e engenharia; atualizar ao mudar códigos de erro ou campos.

---

## 7. Débitos técnicos conscientes

| Débito | **Fato** / **Observação** |
|--------|---------------------------|
| **Persistência** | **Fato:** Apenas `InMemoryTenantRepository`; sem Flyway/JPA. |
| **Segurança** | **Fato histórico da W1.1:** Endpoints `/api/admin/...` sem autenticação/autorização no módulo. **Atualização:** coberto por IAM W1.2 na borda HTTP. |
| **Eventos de domínio** | **Fato:** Nenhum `TenantCreated` / `TenantDeactivated` publicado (outbox, etc.). |
| **`IllegalArgumentException`** | **Fato:** Usado em fluxos de argumento inválido; handler genérico `INVALID_REQUEST` — pode capturar outros `IAE` na mesma cadeia. |
| **Slug como tipo** | **Observação:** Não há value object `Slug`; regra está em `Tenant.canonicalSlug`. |

---

## 8. Riscos arquiteturais

| Risco | **Observação** |
|-------|----------------|
| **Drift multi-BC** | Outro BC importar `adapters.out.persistence` do Tenant quebra isolamento — **Fato:** ArchUnit já restringe (ex.: catalog não depende de persistence de tenant). |
| **Explosão de handlers** | Muitos `@ExceptionHandler(IllegalArgumentException)` globais — monitorar escopo. |
| **Duplicação teste BDD vs web** | Mesmos fluxos em dois estilos — risco de manter só um atualizado. |
| **Operação sem observabilidade** | **Fato:** Não é débito só do Tenant; logging/trace não modelados neste BC. |

**Recomendação:** Antes de JDBC, definir **migration** e **modelo de tabela** `tenants` alinhados ao agregado (slug único, `active`, timestamps futuros).

---

## 9. Critérios de pronto atingidos

| Critério | Estado (**Fato** com base no repositório) |
|----------|------------------------------------------|
| Endpoints implementados | Sim — ver secção 2. |
| Testes automatizados | Sim — unitários, web, BDD, ArchUnit. |
| ArchUnit | Regras presentes em `com.platform.architecture`. |
| BDD executando | Sim — suite Cucumber (`TenantW11BddSuite`) + feature PT. |
| Separação hexagonal | Sim — reforçada por testes de arquitetura. |

**Observação:** “Pronto para produção” **não** é reivindicado — falta persistência, segurança e hardening.

---

## 10. Recomendações para evolução do BC Tenant

### W1.2 ou próximo ciclo (Tenant)

**Recomendação:**

- Persistência **PostgreSQL** + JPA ou JDBC; **constraint UNIQUE (slug)**; transações no use case.  
- **Spring Security** (ou equivalente) para `/api/admin/**`; roles `PLATFORM_ADMIN`.  
- Eventos de domínio ou **outbox** para “tenant criado/desativado” (integração com billing/notificações).  
- **VO `Slug`** ou validação adicional (regex, slugs reservados).  
- Cenário BDD **404 por slug inexistente** (paridade com 404 por id).  
- Opcional: **OpenAPI** gerado alinhado aos DTOs.

### O que não fazer ainda (sem decisão explícita)

**Recomendação:** Não adicionar **listagem paginada** até haver necessidade de produto; não expor **PATCH genérico** de tenant; não introduzir **Kafka** só para eventos de tenant antes de critérios claros (Phase 3 / ADR eventos).

---

## Conclusão

**Observação:** O BC Tenant da W1.1 está **pronto para servir como referência** de estrutura hexagonal, contratos de erro, testes em camadas e BDD **dentro dos limites** Wave 1 (in-memory, sem segurança).

**Recomendação:** Tratar este documento como **linha de base**; revisar após introdução de persistência real e autenticação.
