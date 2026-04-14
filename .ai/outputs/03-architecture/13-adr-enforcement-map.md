# ADR Enforcement Map

## ADR-01 — Greenfield
Objetivo:
- nenhum código legado entra no novo core
Regra:
- Nenhum módulo pode depender de código legado.

Validação:
- inspeção de dependências
- revisão de imports
- CI check: ausência de pacotes legacy
- proibir imports de pacotes legacy
- revisão de dependências
- CI falha se houver dependência proibida

---

## ADR-02 — Modular Monolith
Objetivo:
- bounded contexts não acessam persistência uns dos outros
Regra:
- módulos só se comunicam via contratos definidos
- proibido acesso direto a persistence de outro BC

Validação:
- ArchUnit rules
- testes de boundary

---

## ADR-03 — Hexagonal Architecture
Objetivo:
- domínio não depende de Spring nem de adapters
Regra:
- domínio não depende de framework
- adapters dependem do core, nunca o contrário

Validação:
- ArchUnit: dependência unidirecional
- pacotes separados: domain / application / adapters

---

## ADR-04 — Multi-tenant
Objetivo:
- toda leitura e escrita relevante respeita tenant_id
Regra:
- toda entidade tem tenant_id
- toda query filtra tenant_id

Validação:
- testes de segregação tenant A vs tenant B
- testes de integração com filtros obrigatórios
- testes automáticos de segregação
- interceptors obrigatórios
- testes de vazamento cross-tenant

---

## ADR-05 — Outbox
Regra:
- todo evento persistido via outbox
- nenhum publish direto fora de transação

Validação:
- testes de persistência + publish
- revisão de adapters

---

## ADR-06 — No Kafka (Wave 1)
Regra:
- proibido uso de brokers externos

Validação:
- verificação de dependências

---

## ADR-07 — Payment Isolation
Regra:
- Payment não pode ser chamado diretamente por outros BCs
- comunicação via Application Service

Validação:
- ArchUnit + testes de boundary

---

## ADR-08 — Design System
Regra:
- UI deve usar tokens e primitives
- proibido CSS solto fora do DS

Validação:
- lint rules
- revisão de componentes

---

## ADR-09 — MCP/RAG fora do core
Regra:
- nenhuma chamada de IA no fluxo transacional

Validação:
- revisão de dependências
- testes de latência

## ADR-10 — OpenAPI
Objetivo:
- contrato da API não quebra silenciosamente

Validação:
- geração do spec no build
- falha se não gerar
