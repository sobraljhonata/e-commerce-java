# Multi-tenant Testing Rules

Version: 1.1
Last updated: 2026-04-29
Status: Active (normative)
Owner/purpose: Phase 4 quality guardrail for tenant-scoped increments

## Objetivo
Garantir que todo bounded context tenant-scoped prove isolamento entre tenants por testes automatizados.

## Regra principal
Toda feature que manipula dados associados a tenant deve ter testes explícitos de isolamento.

## Checklist obrigatório antes de implementar
- a entidade/capability pertence a um tenant?
- o tenantId vem exclusivamente do contexto autenticado (JWT)?
- existe risco de entrada de tenantId por request (body/query/path/header)?
- quais testes comprovam isolamento entre tenant correto e outro tenant?

## Operações obrigatórias

### Create
- tenantId nunca deve vir do request.
- tenantId deve vir do contexto autenticado.
- se o request enviar tenantId, ele deve ser ignorado ou rejeitado conforme política definida.
- deve haver teste protegendo essa decisão.

### Read by id
- tenant A não pode ler recurso do tenant B.
- resposta esperada: 404, não 403, para evitar enumeração.

### List
- tenant A deve listar apenas seus próprios recursos.
- tenant B nunca pode aparecer na listagem de tenant A.
- lista vazia deve retornar 200 [].

### Update
- tenant A não pode atualizar recurso do tenant B.
- resposta esperada: 404.
- tenantId não pode ser alterado.

### Delete / deactivate
- tenant A não pode remover ou desativar recurso do tenant B.
- resposta esperada: 404.

## Testes mínimos
Para toda capability tenant-scoped, criar pelo menos:

- teste de tenant correto para a operação;
- teste de outro tenant para a operação (isolamento explícito);
- teste unitário do use case validando uso do tenantId do contexto;
- teste do repositório validando filtro/lookup por tenant;
- teste web/integration validando acesso cross-tenant;
- teste de payload malicioso com tenantId (ignora ou rejeita com política explícita);
- cenário BDD quando o fluxo for parte do comportamento principal.

## Regra de segurança
Nunca aceitar tenantId vindo de:
- body
- query param
- path param
- header arbitrário

A fonte de verdade da requisição autenticada é o JWT validado.

## Política de resposta
Para recurso existente em outro tenant, responder:
- 404 Not Found

Não revelar se o recurso existe em outro tenant.

## Critério de aprovação
Nenhum incremento tenant-scoped deve ser aprovado sem evidência de isolamento multi-tenant em testes.