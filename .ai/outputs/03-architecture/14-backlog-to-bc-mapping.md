# Backlog → Bounded Context Mapping (estado atual)

Referências:
- Arquitetura oficial: `.ai/outputs/03-architecture/01-bounded-contexts-oficiais.md`
- Backlog de produto: `.ai/outputs/02-product/04-backlog-epicos.md`, `05-backlog-features.md`, `06-backlog-historias.md`
- Implementação guiada: `.ai/outputs/05-guided-implementation/`

## Status por capability da Wave 1

| ID | Capability | BC | Situação | Evidência |
|----|------------|----|----------|-----------|
| C01 | Tenant foundation (create/get by id/get by slug/update status) | BC01 Tenant & Platform Config | ✅ Concluído | `tenant-w1.1-closeout.md` |
| C02 | IAM admin login + JWT + proteção `/api/admin/**` | BC02 Identity & Access | ✅ Concluído (baseline) | `iam-w1.2-increment-2-closeout.md` |
| C03 | Catalog Product tenant-aware (create/get by id/list/update) | BC03 Catalog | ✅ Concluído (Wave 1 atual) | closeout Catalog W1 em `05-guided-implementation` |
| C04 | Catalog Category tenant-aware (create/get by id) | BC03 Catalog | ✅ Concluído (Wave 1 atual) | closeout Catalog W1 em `05-guided-implementation` |
| C05 | Hardening multi-tenant (cross-tenant + payload malicioso tenantId) | BC03 Catalog | ✅ Concluído (escopo atual) | closeout Catalog W1 em `05-guided-implementation` |
| C06 | Pricing base | BC04 Pricing & Promotions | ⏳ Planejado | backlog Phase 2 / MVP |
| C07 | Cart | BC05 Cart | ⏳ Planejado | backlog Phase 2 / MVP |
| C08 | Order | BC06 Order | ⏳ Planejado | backlog Phase 2 / MVP |
| C09 | Payment sandbox | BC07 Payment | ⏳ Planejado | backlog Phase 2 / MVP |
| C10 | Notifications (pedido) | BC10 Notifications | ⏳ Planejado | backlog Phase 2 / MVP |
| C11 | Integration Hub (Sebrae) | BC11 Integration Hub | ❌ Fora da Wave 1 inicial | backlog Phase 2 / arquitetura |

## Regras de rastreabilidade

1. Toda capability concluída na Wave 1 deve apontar para closeout em `05-guided-implementation`.
2. Capability tenant-scoped deve trazer evidência do checklist de:
   - `.ai/context/19-multi-tenant-testing-rules.md`
   - `.ai/context/20-multi-tenant-validation-checklist-template.md`
3. Mudança de status de capability deve manter referência a BC oficial (`01-bounded-contexts-oficiais.md`).