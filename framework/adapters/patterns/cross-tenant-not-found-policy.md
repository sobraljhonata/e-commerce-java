# Cross-Tenant Not Found Policy

## 1. Nome do pattern

**Cross-Tenant Not Found Policy**

## 2. Problema

Como evitar que um usuário descubra se um recurso existe em outro tenant (enumeração e vazamento de existência) em um sistema multi-tenant.

## 3. Contexto de uso

Aplicável quando:

- recursos pertencem a tenant;
- recurso é acessado por id;
- API é multi-tenant;
- o sistema precisa evitar enumeração de recursos.

## 4. Solução

- Sempre buscar recurso usando `tenantId` do contexto autenticado + `resourceId`.
- Se não encontrar no escopo do tenant, retornar **404**.
- Não distinguir publicamente:
  - recurso inexistente;
  - recurso existente em outro tenant.
- Não retornar **403** para recurso de outro tenant quando isso revelar existência.

## 5. Política de erro

- **404 Not Found**
  - recurso inexistente;
  - recurso existente em outro tenant;
  - referência cruzada inexistente ou de outro tenant.

- **400 Bad Request**
  - recurso existe no tenant, mas viola regra de negócio.
  - Exemplo: `Category` existe no tenant e está inativa para associação.

- **401 Unauthorized**
  - ausência ou invalidade de autenticação.

- **403 Forbidden**
  - usuário autenticado existe, contexto válido, recurso pertence ao tenant ou escopo conhecido, mas falta permissão funcional.

## 6. Exemplo concreto

Exemplos (caso real do projeto, como referência):

- `GET /api/admin/products/{id}`: busca por `tenantId + productId`, retorna `404` quando o produto não é acessível no tenant.
- `GET /api/admin/categories/{id}`: busca por `tenantId + categoryId`, retorna `404` quando a categoria não é acessível no tenant.
- `Product -> Category` com `categoryId` de outro tenant: validação tenant-aware retorna `404` público (`CATEGORY_NOT_FOUND`), sem revelar a existência real em outro tenant.

## 7. Pseudocódigo genérico

```java
tenantId = currentUser.tenantId()
resource = repository.findByIdAndTenant(tenantId, resourceId)
if resource.empty:
    throw ResourceNotFound
```

## 8. Testes obrigatórios

- tenant A não lê recurso de tenant B → **404**.
- tenant A não atualiza recurso de tenant B → **404**.
- tenant A não lista recurso de tenant B.
- referência cruzada para recurso de outro tenant → **404**.
- recurso inexistente e recurso de outro tenant têm a mesma resposta pública.
- a resposta não deve revelar “exists in another tenant”.

## 9. Anti-patterns

- Buscar por id global e depois decidir `403`.
- Retornar mensagem “resource belongs to another tenant”.
- Retornar `403` para recurso de outro tenant quando isso revela existência.
- Usar `tenantId` do request para decidir acesso.
- Vazar metadados de existência em logs públicos ou na resposta.

## 10. Relação com outros patterns

- **Depende de:** `Tenant Context Enforcement` (o `tenantId` efetivo deve vir do contexto autenticado).
- **Complementa:** `Aggregate Reference Validation` (referências cruzadas também devem ter comportamento público equivalente para inexistente vs outro tenant).
- Deve ser aplicado em:
  - leitura por id tenant-scoped;
  - updates tenant-scoped;
  - queries/listagens tenant-scoped (evitar leaks indiretos).

## 11. Quando NÃO aplicar

Casos raros, que exigem decisão explícita registrada:

- recurso realmente público/global;
- API interna com auditoria e política diferente;
- contexto administrativo global explicitamente modelado.

## 12. Relação com governança

Este pattern deve ser aplicado junto com os artefatos normativos:

- `./.ai/outputs/03-architecture/16-platform-implementation-standards.md` (ADR 16)
- `./.ai/context/19-multi-tenant-testing-rules.md`
- `./.ai/context/20-multi-tenant-validation-checklist-template.md`
- `./.ai/context/24-increment-classification.md`
- `./.ai/context/25-definition-of-done.md`
- `./.ai/context/26-agent-self-check.md`

## 13. Leak Vectors

Mesmo com 404 padronizado, vazamento de existência pode ocorrer indiretamente.

Cuidados adicionais:

- tamanho de resposta não deve variar entre inexistente e outro tenant
- tempo de resposta não deve ser significativamente diferente
- mensagens de erro não devem indicar causa real
- logs expostos (ex: APIs públicas) não devem revelar tenant real
- mensagens internas devem ser separadas das externas

Este pattern cobre apenas o contrato HTTP externo; outros canais também devem ser considerados.
