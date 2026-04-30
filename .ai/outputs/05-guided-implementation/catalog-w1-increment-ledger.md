# BC Catalog — Increment ledger (Wave 1)

Objetivo: registrar granularidade de incrementos do Catalog sem duplicar closeouts completos.

---

## Regra de uso

- Este ledger complementa o closeout consolidado:
  - `.ai/outputs/05-guided-implementation/catalog-w1-closeout.md`
- Criar closeout separado por incremento somente quando houver decisão arquitetural relevante adicional, risco alto novo, ou mudança de escopo que exija histórico detalhado.

---

## Incrementos registrados

| Incremento | Escopo | Situação | Evidência principal |
|---|---|---|---|
| C-W1-I1 | Product create tenant-aware | Concluído | `catalog-w1-closeout.md` |
| C-W1-I2 | Product get by id tenant-aware | Concluído | `catalog-w1-closeout.md` |
| C-W1-I3 | Product list tenant-aware | Concluído | `catalog-w1-closeout.md` |
| C-W1-I4 | Product update tenant-aware | Concluído | `catalog-w1-closeout.md` |
| C-W1-I5 | Category create + get by id tenant-aware | Concluído | `catalog-w1-closeout.md` |
| C-W1-I5-H1 | Hardening multi-tenant (cross-tenant 404 + payload malicioso tenantId) | Concluído | `catalog-w1-closeout.md` |

---

## Nota de rastreabilidade

- Tenant baseline: `tenant-w1.1-closeout.md`
- IAM baseline: `iam-w1.2-increment-2-closeout.md`
- Regras multi-tenant: `.ai/context/19-multi-tenant-testing-rules.md`
- Template checklist: `.ai/context/20-multi-tenant-validation-checklist-template.md`
