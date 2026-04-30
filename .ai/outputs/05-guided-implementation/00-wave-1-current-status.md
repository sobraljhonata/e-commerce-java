# Wave 1 — Estado atual consolidado

Documento de referência rápida para alinhar contexto, agentes e closeouts ao estado real da implementação.

---

## Fases

- Phase 1 discovery: concluída
- Phase 2 product: concluída
- Phase 3 greenfield: concluída
- Phase 4 guided implementation: em execução

---

## Bounded Contexts com implementação em andamento

| BC | Status atual | Evidência |
|---|---|---|
| Tenant (W1.1) | Concluído (baseline) | `tenant-w1.1-closeout.md` |
| IAM (W1.2) | Concluído baseline (login + JWT + proteção admin + contexto autenticado) | `iam-w1.2-increment-2-closeout.md` |
| Catalog (W1 atual) | Product tenant-aware (create/get/list/update) + Category tenant-aware (create/get by id) | `catalog-w1-closeout.md` |

---

## Decisões ativas preservadas

1. Monólito modular + arquitetura hexagonal por BC.
2. JWT como fonte de contexto autenticado.
3. `tenantId` vem exclusivamente do JWT/contexto autenticado em operações tenant-scoped.
4. Recurso de outro tenant retorna 404.

---

## Regras obrigatórias para incrementos tenant-scoped

Fontes de verdade:
- `.ai/context/19-multi-tenant-testing-rules.md`
- `.ai/context/20-multi-tenant-validation-checklist-template.md`

Regras de aprovação:
- checklist multi-tenant obrigatório no output;
- fail-fast: qualquer `Missing` aplicável impede decisão `Approved`.

---

## Rastreabilidade mínima recomendada por novo incremento

1. Planejamento/decisão arquitetural (quando aplicável).
2. Execução com testes (unit, web/integration, BDD/ArchUnit quando aplicável).
3. Closeout em `05-guided-implementation`.
4. Checklist multi-tenant preenchido quando o incremento for tenant-scoped.
