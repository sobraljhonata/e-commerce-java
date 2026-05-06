# Catalog XA Closeout — Product só pode usar categoria ativa

Version: 1.0  
Date: 2026-05-05  
Status: Concluded

## 1. Objetivo

Documentar o incremento que adiciona uma regra de negócio entre agregados tenant-aware no Catalog: um `Product` só pode referenciar `Category` ativa.

**Tipo classificado:** `XA` (Cross-aggregate / reference validated), pois a associação `Product -> Category` exige validação de referência no mesmo tenant e regra de domínio adicional (`active = true`).

**DoD status:** Done.  
**Pendências / riscos:** não há bloqueio funcional aberto; existe débito consciente de extração de validador comum de referência.

## 2. Decisões arquiteturais

- A regra foi implementada na camada de aplicação (`CreateProductUseCase` e `UpdateProductUseCase`), mantendo arquitetura hexagonal.
- A validação de referência continua tenant-aware via `CategoryRepository.findByIdAndTenant(tenantId, categoryId)`.
- Foi introduzida exceção de domínio específica para regra de atividade (`CategoryInactiveException`) para separar claramente:
  - inexistência/isolamento de tenant;
  - violação de regra de negócio.
- A origem de `tenantId` permanece exclusivamente do contexto autenticado (JWT via `CurrentUserProvider`).

## 3. Sequência TDD

1. Red: inclusão de cenários de falha para categoria inativa em testes unitários (create/update use case).
2. Red: inclusão de contratos HTTP para categoria inativa em testes web e integração.
3. Red: inclusão de cenário BDD para tentativa de cadastro com categoria inativa.
4. Green: implementação da regra nos use cases e tratamento de erro HTTP (`CATEGORY_INACTIVE`).
5. Refactor leve: ajuste de formatação com Spotless sem alteração comportamental adicional.

## 4. Implementação

- Regra de negócio implementada: se `categoryId` for informado, a categoria precisa:
  1. existir no tenant autenticado;
  2. estar ativa.
- Distinção de resposta:
  - `404 CATEGORY_NOT_FOUND`: categoria inexistente ou pertencente a outro tenant;
  - `400 CATEGORY_INACTIVE`: categoria existe no tenant, porém inativa.
- A regra se aplica tanto no fluxo de criação quanto no de atualização de produto.

## 5. Arquivos criados/alterados

**Criado**
- `backend/src/main/java/com/platform/catalog/domain/CategoryInactiveException.java`

**Alterados**
- `backend/src/main/java/com/platform/catalog/application/CreateProductUseCase.java`
- `backend/src/main/java/com/platform/catalog/application/UpdateProductUseCase.java`
- `backend/src/main/java/com/platform/catalog/adapters/in/web/CatalogExceptionHandler.java`
- `backend/src/main/java/com/platform/catalog/adapters/in/web/CatalogApiErrorResponse.java`
- `backend/src/test/java/com/platform/catalog/application/CreateProductUseCaseTest.java`
- `backend/src/test/java/com/platform/catalog/application/UpdateProductUseCaseTest.java`
- `backend/src/test/java/com/platform/catalog/adapters/in/web/ProductControllerWebTest.java`
- `backend/src/test/java/com/platform/catalog/adapters/in/web/ProductCategoryAssociationApiIntegrationTest.java`
- `backend/src/test/java/com/platform/bdd/catalog/CatalogProductStepDefinitions.java`
- `backend/src/test/resources/features/bdd/catalog/catalog_create_product_w1.feature`

## 6. Testes executados

- `mvn spotless:apply`
- `mvn -Dtest=CreateProductUseCaseTest,UpdateProductUseCaseTest,ProductControllerWebTest,ProductCategoryAssociationApiIntegrationTest,CatalogCucumberTest test`

Resultado: suites executadas com sucesso para os cenários do incremento XA.

## 7. Validação manual

1. Autenticar usuário admin e obter JWT.
2. Criar categoria ativa e tentar criar/atualizar produto com esse `categoryId` -> sucesso.
3. Criar categoria inativa e tentar criar/atualizar produto com esse `categoryId` -> `400` com `code = CATEGORY_INACTIVE`.
4. Usar `categoryId` inexistente ou de outro tenant -> `404` com `code = CATEGORY_NOT_FOUND`.

## 8. Multi-tenant validation checklist

| Operação | Regra obrigatória | Teste/evidência | Status | Decisão |
|---|---|---|---|---|
| Create | tenantId vem do JWT/contexto, nunca do request | `CreateProductUseCaseTest.persists_product_with_tenant_from_authenticated_user` + `ProductCategoryAssociationApiIntegrationTest.create_with_valid_category_same_tenant` | OK | Mantido sem mudança de contrato de tenant source |
| Create | payload com tenantId é ignorado ou rejeitado conforme política definida | Cobertura pré-existente de request DTO sem tenantId e uso exclusivo de contexto autenticado; incremento XA não altera superfície de tenant no payload | OK | Sem regressão na política anti-spoofing |
| Read by id | recurso de outro tenant retorna 404 | Não alterado por este incremento; cobertura existente de Product por tenant no BC Catalog | N/A | Fora do escopo funcional do XA atual |
| List | retorna somente recursos do tenant autenticado | Não alterado por este incremento; cobertura existente de listagem tenant-aware | N/A | Fora do escopo funcional do XA atual |
| Update | recurso de outro tenant retorna 404 | `UpdateProductUseCaseTest.with_category_from_other_tenant_returns_not_found` + `ProductCategoryAssociationApiIntegrationTest.update_with_other_tenant_category_returns_404` | OK | Isolamento cross-tenant preservado |
| Delete/deactivate | recurso de outro tenant retorna 404 | Operação não implementada no Product W1 | N/A | Sem aplicabilidade no estado atual |
| Tenant source | tenantId vem exclusivamente do JWT/contexto autenticado | `CreateProductUseCase`/`UpdateProductUseCase` continuam usando `CurrentUserProvider` | OK | Regra arquitetural preservada |
| Tenant leak prevention | não há tenantId confiável vindo de body, query, path ou header arbitrário | Contrato de request permanece sem `tenantId`; validação de categoria via tenant do contexto | OK | Sem vetor novo de vazamento |

## 9. Observações arquiteturais

- **Débito consciente:** possível extração futura de um `CategoryReferenceValidator` (ou componente equivalente de aplicação) para reduzir duplicação entre `CreateProductUseCase` e `UpdateProductUseCase`.
- **Critério para extração:** quando surgirem novos fluxos que validem referência de categoria (ex.: bulk update, importações, novas operações XA), para evitar espalhamento de regra.
- **Risco residual atual:** baixo; duplicação está controlada e coberta por testes, mas a extração melhoraria manutenção no médio prazo.
