# Tenant Context Enforcement

## 1. Nome do pattern

**Tenant Context Enforcement**

## 2. Problema

Como impedir que clientes escolham, forjem ou desviem o `tenantId` efetivo em operações tenant-scoped, causando vazamento de dados entre tenants.

## 3. Contexto de uso

Aplicável quando:

- o sistema é multi-tenant;
- dados pertencem a tenant;
- usuário autenticado opera dentro de um tenant;
- API manipula recursos tenant-scoped.

## 4. Solução

- `tenantId` deve ser extraído do contexto autenticado.
- JWT/sessão/provider de autenticação é a fonte de verdade para tenant efetivo.
- Requests não devem tratar `tenantId` como autoridade de escopo.
- Use cases recebem/resolvem `tenantId` internamente (ex.: via `CurrentUserProvider`).
- Repositórios devem ser tenant-aware (ex.: `findByIdAndTenant`, `findAllByTenant`).
- DTOs de entrada não devem expor `tenantId` como campo de controle.

## 5. Política de request

- **body:** não aceitar `tenantId` como fonte de verdade.
- **query:** não aceitar `tenantId` como filtro de segurança.
- **path:** não usar `tenantId` para escopo de segurança.
- **header:** não confiar em headers arbitrários de tenant (ex.: `X-Tenant-Id` enviado pelo cliente).

## 6. Política de erro

- Recurso de outro tenant deve retornar **404**.
- Contexto autenticado ausente/inválido deve retornar **401**.
- Acesso autenticado sem permissão deve retornar **403** quando aplicável à política de autorização.

## 7. Exemplo concreto

Caso real de referência:

- Catalog (`Product`/`Category`) resolve tenant via `AuthenticatedUser`.
- Operações tenant-scoped usam `tenantId` do contexto autenticado.
- Acesso cross-tenant é tratado como `404`.

## 8. Pseudocódigo genérico

```java
AuthenticatedUser user = currentUserProvider.currentUser()
UUID tenantId = user.tenantId()
repository.findByIdAndTenant(tenantId, resourceId)
```

## 9. Testes obrigatórios

- request com `tenantId` malicioso não altera tenant efetivo;
- tenant A não lê recurso de tenant B;
- tenant A não lista recurso de tenant B;
- tenant A não atualiza recurso de tenant B;
- ausência de token retorna 401;
- recurso de outro tenant retorna 404.

## 10. Anti-patterns

- Aceitar `tenantId` no body de operações tenant-scoped.
- Aceitar `tenantId` em query como mecanismo de isolamento.
- Confiar em `X-Tenant-Id` vindo do cliente.
- Buscar por `id` sem escopo de tenant e filtrar depois no controller.
- Retornar `403` para recurso de outro tenant, revelando existência indevida.

## 11. Relação com governança

Este pattern deve ser aplicado junto com os artefatos normativos:

- `./.ai/outputs/03-architecture/16-platform-implementation-standards.md` (ADR 16)
- `./.ai/context/19-multi-tenant-testing-rules.md`
- `./.ai/context/20-multi-tenant-validation-checklist-template.md`
- `./.ai/context/24-increment-classification.md`
- `./.ai/context/25-definition-of-done.md`
- `./.ai/context/26-agent-self-check.md`

## 12. Quando aplicar

- todo BC tenant-scoped;
- todo endpoint admin tenant-aware;
- toda query/listagem de dados tenant-scoped;
- toda referência cruzada entre agregados tenant-scoped.

## Enforcement Boundary

A aplicação deve garantir que o tenantId é resolvido antes de qualquer lógica de negócio.

Responsabilidades:

- Camada de autenticação (ex: filtro JWT):
  - valida token
  - popula contexto autenticado

- Camada de aplicação (use case):
  - consome tenantId do contexto
  - nunca aceita tenantId externo

- Camada de domínio:
  - não conhece tenantId como origem de decisão externa
  - apenas recebe valores já validados

Qualquer desvio dessa separação pode comprometer o isolamento multi-tenant.
