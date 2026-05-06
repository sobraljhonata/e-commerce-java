# Aggregate Reference Validation

## 1. Nome do pattern

**Aggregate Reference Validation**

## 2. Problema

Como validar referência entre agregados no mesmo tenant sem acoplar agregados diretamente, preservando isolamento multi-tenant e regras de domínio.

## 3. Contexto de uso

Aplicável quando:

- Aggregate A referencia Aggregate B por id.
- Ambos são tenant-scoped.
- B precisa existir no mesmo tenant.
- B pode exigir regras adicionais (ex.: `active = true`) antes de permitir associação.

## 4. Solução

- Aggregate A guarda apenas `bId`.
- Aggregate A não carrega Aggregate B.
- O use case valida B por repository tenant-aware.
- O repository expõe lookup no formato `findByIdAndTenant(tenantId, bId)`.
- Regras adicionais de B (estado, flags, disponibilidade) são avaliadas no use case.
- Persistir Aggregate A somente após a validação completa.

## 5. Política de erro

- **404**: referência inexistente ou de outro tenant.
- **400**: referência existe no tenant, mas viola regra de negócio (ex.: inativa).
- Nunca revelar explicitamente que o recurso existe em outro tenant.

## 6. Exemplo concreto

Caso real do projeto:

- `Product` referencia `Category` por `categoryId`.
- `Product` não carrega `Category`.
- `Category` é validada via `CategoryRepository.findByIdAndTenant(tenantId, categoryId)`.
- Se não existir no tenant atual: `404 CATEGORY_NOT_FOUND`.
- Se existir no tenant, mas inativa: `400 CATEGORY_INACTIVE`.
- A validação fica na camada de aplicação (use case), não no controller.

## 7. Pseudocódigo genérico

```java
tenantId = currentUser.tenantId()
reference = referenceRepository.findByIdAndTenant(tenantId, referenceId)
if reference not found:
    throw ReferenceNotFound
if !reference.active:
    throw ReferenceInactive
aggregate = Aggregate.create(..., referenceId)
```

## 8. Testes obrigatórios

- cria/atualiza com referência válida.
- referência inexistente retorna 404.
- referência de outro tenant retorna 404.
- referência inativa retorna 400.
- `tenantId` não vem do request.
- payload malicioso não altera tenant.

## 9. Anti-patterns

- Carregar Aggregate B dentro de Aggregate A.
- Aceitar `tenantId` do request.
- Validar referência somente por id (`findById`) sem escopo de tenant.
- Retornar `403` para referência de outro tenant e vazar semântica de existência.
- Deixar controller validar regra de domínio de associação.

## 10. Quando extrair helper

Criar um componente como `ReferenceValidator` faz sentido quando:

- A mesma validação de referência é repetida em múltiplos use cases.
- A mesma política de erro (`404` vs `400`) se repete em vários fluxos.
- O BC passa a ter mais de duas referências cruzadas com padrão semelhante.

Caso contrário, manter a validação explícita no use case pode preservar legibilidade e baixo acoplamento.

## 11. Relação com a governança

Este pattern operacionaliza decisões já definidas e deve ser usado em conjunto com:

- `./.ai/outputs/03-architecture/16-platform-implementation-standards.md` (ADR 16)
- `./.ai/context/19-multi-tenant-testing-rules.md`
- `./.ai/context/20-multi-tenant-validation-checklist-template.md`
- `./.ai/context/24-increment-classification.md` (tipo `XA`)
- `./.ai/context/25-definition-of-done.md`
- `./.ai/context/26-agent-self-check.md`

Referência de origem (caso concreto que motivou este pattern):

- `./.ai/outputs/05-guided-implementation/catalog-xa-active-category-rule-closeout.md`
