# ADR Enforcement Map

Este mapa operacionaliza os ADRs aceitos em:
- `.ai/outputs/03-architecture/12-adrs-principais.md`

## ADR-001 — Greenfield sem reuso de código legado
- Regra: novo core não depende de código legado.
- Enforcement: revisão de imports/dependências + check de CI para pacotes proibidos.

## ADR-002 — Monólito modular como padrão inicial
- Regra: BCs não acessam persistence de outros BCs.
- Enforcement: ArchUnit de boundary + revisão de uso de contratos/ports.

## ADR-003 — Stack Java 21 / Spring Boot 3 / Angular / Postgres / Redis / AWS
- Regra: manter baseline tecnológica da plataforma na Wave 1.
- Enforcement: revisão de dependências e ADR complementar quando houver exceção.

## ADR-004 — Multi-tenant por `tenant_id` com enforcement na aplicação
- Regra: leitura/escrita tenant-scoped sempre com escopo de tenant.
- Enforcement técnico: testes unitários/use case + repository + integração cross-tenant.
- Enforcement de output: checklist obrigatório de `.ai/context/20-multi-tenant-validation-checklist-template.md` para incrementos tenant-scoped.

## ADR-005 — Hexagonal por bounded context
- Regra: domínio sem framework; adapters dependem do core, nunca o contrário.
- Enforcement: ArchUnit de dependência unidirecional + estrutura de pacotes por BC.

## ADR-006 — Outbox + sem Kafka na Wave 1
- Regra: sem broker externo na Wave 1; eventos externos via outbox.
- Enforcement: revisão de dependências + testes do fluxo de persistência/outbox quando aplicável.

## ADR-007 — Sem SAGA distribuída no MVP
- Regra: evitar coreografia distribuída no MVP.
- Enforcement: revisão arquitetural; exigir justificativa formal para exceções.

## ADR-008 — Payment isolado em BC dedicado
- Regra: BC de pagamento com fronteira própria e integrações externas concentradas.
- Enforcement: ArchUnit de boundary + testes de contrato/integridade de fluxo.

## ADR-009 — Design system de plataforma + tenant theming limitado
- Regra: UI deve usar tokens/primitives; theming controlado por política.
- Enforcement: lint/review de UI + check de aderência ao design system.

## ADR-010 — IA (MCP/RAG) fora do core transacional
- Regra: IA fora de auth/pagamento/consistência transacional.
- Enforcement: revisão de desenho + checklist de risco operacional.

## ADR-011 — Integrações Sebrae via Integration Hub
- Regra: BC11 é fronteira oficial para integrações legadas/externas.
- Enforcement: revisão de arquitetura + proibição de acesso direto a integrações fora do hub.

---

## Nota
`OpenAPI` permanece prática recomendada de governança de contrato, mas não é ADR principal listado em `12-adrs-principais.md`.
