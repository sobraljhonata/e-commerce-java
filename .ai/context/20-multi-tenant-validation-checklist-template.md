# Multi-tenant Validation Checklist Template

Version: 1.1
Last updated: 2026-04-29
Status: Active (normative template)
Owner/purpose: Mandatory output template for tenant-scoped delivery/review

Toda entrega ou revisão de incremento tenant-scoped deve incluir esta seção literal.

## Multi-tenant validation checklist

| Operação | Regra obrigatória | Teste/evidência | Status | Decisão |
|---|---|---|---|---|
| Create | tenantId vem do JWT/contexto, nunca do request |  | OK / N/A / Missing |  |
| Create | payload com tenantId é ignorado ou rejeitado conforme política definida |  | OK / N/A / Missing |  |
| Read by id | recurso de outro tenant retorna 404 |  | OK / N/A / Missing |  |
| List | retorna somente recursos do tenant autenticado |  | OK / N/A / Missing |  |
| Update | recurso de outro tenant retorna 404 |  | OK / N/A / Missing |  |
| Delete/deactivate | recurso de outro tenant retorna 404 |  | OK / N/A / Missing |  |
| Tenant source | tenantId vem exclusivamente do JWT/contexto autenticado |  | OK / N/A / Missing |  |
| Tenant leak prevention | não há tenantId confiável vindo de body, query, path ou header arbitrário |  | OK / N/A / Missing |  |

## Approval rule

- Se houver `Missing` em operação aplicável, a decisão não pode ser “aprovado sem ressalvas”.
- Se faltar teste cross-tenant para operação tenant-scoped aplicável, marcar risco obrigatório.
- Se o tenantId vier do request, rejeitar o incremento.
- Se recurso de outro tenant retornar algo diferente de 404, justificar ou rejeitar.