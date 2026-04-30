# Fechamento técnico — BC Catalog (Wave 1 atual)

**Escopo:** consolidar o estado implementado até agora no BC Catalog, mantendo rastreabilidade com Tenant W1.1 e IAM W1.2.  
**Base:** implementação atual no `backend`, testes unit/web/integração/BDD e hardening multi-tenant já aplicado.

---

## 1. Estado atual do BC Catalog

### Product (tenant-aware)
- Create
- Get by id
- List
- Update

### Category (tenant-aware)
- Create
- Get by id

### Integração com IAM
- `tenantId` vem do contexto autenticado (`CurrentUserProvider` / `AuthenticatedUser`).
- `tenantId` não é aceito como fonte de verdade no request.

---

## 2. Decisões preservadas

1. **Arquitetura hexagonal por BC** (domínio, aplicação, ports, adapters).
2. **Sem acoplamento direto com BC Tenant** (somente contexto autenticado).
3. **Product e Category desacoplados** nesta fase (sem vínculo obrigatório entre agregados).
4. **Política de isolamento:** recurso de outro tenant retorna 404.

---

## 3. Evidências de testes

- **Unitários (domínio/aplicação):**
  - criação/validações de `Product` e `Category`
  - use cases usando tenant do contexto autenticado
- **Repositório in-memory:**
  - filtro por tenant em lookup/listagem
- **Web/integração:**
  - endpoints admin com JWT
  - comportamento 404 para não encontrado
- **Hardening multi-tenant (Catalog):**
  - cross-tenant: tenant A cria, tenant B não acessa (404)
  - payload malicioso com `tenantId` no POST: valor não influencia resultado
- **BDD mínimo:**
  - fluxos principais de criação/consulta por contexto autenticado

---

## 4. Multi-tenant validation checklist

| Operação | Regra obrigatória | Teste/evidência | Status | Decisão |
|---|---|---|---|---|
| Create | tenantId vem do JWT/contexto, nunca do request | Create Product/Category via use case + integração | OK | Mantida |
| Create | payload com tenantId é ignorado ou rejeitado conforme política definida | Integração Catalog valida payload malicioso não influencia tenant | OK | Mantida |
| Read by id | recurso de outro tenant retorna 404 | Integração cross-tenant de Category | OK | Mantida |
| List | retorna somente recursos do tenant autenticado | List Product tenant-aware | OK | Mantida |
| Update | recurso de outro tenant retorna 404 | cobertura parcial no Product; manter expansão quando houver caso explícito cross-tenant no endpoint | N/A | Planejar quando ampliar escopo |
| Delete/deactivate | recurso de outro tenant retorna 404 | operação ainda não implementada no Catalog Wave 1 | N/A | Fora de escopo |
| Tenant source | tenantId vem exclusivamente do JWT/contexto autenticado | `CurrentUserProvider` em use cases | OK | Mantida |
| Tenant leak prevention | não há tenantId confiável vindo de body, query, path ou header arbitrário | requests de create sem tenant como fonte de verdade | OK | Mantida |

## Approval rule

- Se houver `Missing` em operação aplicável, a decisão não pode ser “aprovado sem ressalvas”.
- Se faltar teste cross-tenant para operação tenant-scoped aplicável, marcar risco obrigatório.
- Se o tenantId vier do request, rejeitar o incremento.
- Se recurso de outro tenant retornar algo diferente de 404, justificar ou rejeitar.

---

## 5. Débitos técnicos abertos (Catalog)

1. Consolidar closeouts por incremento histórico (se desejado) para rastreio fino.
2. Expandir cenários cross-tenant para todas as operações futuras (update/delete quando entrarem).
3. Manter contrato de erro consistente entre recursos (`PRODUCT_NOT_FOUND`, `CATEGORY_NOT_FOUND`).

---

## 6. Rastreabilidade

- Tenant baseline: `.ai/outputs/05-guided-implementation/tenant-w1.1-closeout.md`
- IAM baseline: `.ai/outputs/05-guided-implementation/iam-w1.2-increment-2-closeout.md`
- Regras multi-tenant: `.ai/context/19-multi-tenant-testing-rules.md`
- Template checklist: `.ai/context/20-multi-tenant-validation-checklist-template.md`
