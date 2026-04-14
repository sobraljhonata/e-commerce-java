# Blueprint detalhado — Wave 1

**Duração orientativa:** 8–12 semanas para time pequeno-médio (ajustar ao velocity). **Meta:** entregar **MVP1** definido em `10-mvp-da-plataforma.md`.  

---

## Princípios da Wave 1

1. **Foundation antes de feature:** repositório, CI, ambientes, OTel, tenant middleware, pacote DS L0–L2.  
2. **Um BC piloto completo em hexágono:** **Catalog** como referência arquitetural.  
3. **Checkout vertical slice** cedo: Order + Payment + notificação, mesmo que catálogo ainda simples.  

---

## Fases dentro da Wave 1

### Fase W1.0 — Plataforma (semanas 1–2)

- BC: Plataforma
- Dependências: Tenant, IAM
- Eventos: ProductCreated
- Testes obrigatórios:
  - tenant isolation
  - validação de produto
- NFR:
  - latência < 100ms
- Riscos:
  - mistura com Pricing

| Entrega | Dono sugerido | Critério de pronto |
|---------|---------------|-------------------|
| Mono-repo backend + módulos BC vazios | Eng | Build + testes smoke |
| PostgreSQL + migrations iniciais + schemas por BC | Eng | Flyway aplicado em dev/stage |
| OIDC integrado; tenant resolution; `tenant_id` em requests | Eng + Sec | Teste de contexto tenant |
| Angular app shell + DS tokens + tema placeholder | Front | Storybook com primitives |
| Pipeline CI (lint, test, build, scan deps) | DevOps | Main protegido |
| Observabilidade: OTel + logs JSON + dashboard mínimo | DevOps | Trace de health → DB |

### Fase W1.1 — Catalog + Storefront leitura (semanas 3–4)

- BC: Catalog + Sotrefront leitura
- Dependências: Tenant, IAM
- Eventos: ProductCreated
- Testes obrigatórios:
  - tenant isolation
  - validação de produto
- NFR:
  - latência < 100ms
- Riscos:
  - mistura com Pricing

| Entrega | Critério de pronto |
|---------|-------------------|
| CRUD produto (API + admin mínimo) | CRUD E2E |
| Listagem + detalhe na vitrine | Lighthouse básico; a11y smoke |
| Upload imagem S3 | URL assinada ou público controlado |
| ArchUnit / regras de dependência entre BCs | CI falha em violação |

### Fase W1.2 — Cart + Order (semanas 5–6)

- BC: Cart + Order
- Dependências: Tenant, IAM
- Eventos: ProductCreated
- Testes obrigatórios:
  - tenant isolation
  - validação de produto
- NFR:
  - latência < 100ms
- Riscos:
  - mistura com Pricing

| Entrega | Critério de pronto |
|---------|-------------------|
| Carrinho com expiração e limites | Teste integração Redis |
| Conversão cart → order | Máquina de estados testada |
| Auditoria de mudança de estado | Log/audit table |

### Fase W1.3 — Payment + notificações (semanas 7–8)

- BC: Payment + notificações
- Dependências: Tenant, IAM
- Eventos: ProductCreated
- Testes obrigatórios:
  - tenant isolation
  - validação de produto
- NFR:
  - latência < 100ms
- Riscos:
  - mistura com Pricing

| Entrega | Critério de pronto |
|---------|-------------------|
| PSP sandbox + webhook + idempotência | Testes com payloads reais de sandbox |
| E-mail “pedido confirmado” | Fila/outbox processando |
| Pós-compra stub (BC09) | Estado visível ao comprador |

### Fase W1.4 — Endurecimento (semanas 9–10)

- BC: Endurecimento
- Dependências: Tenant, IAM
- Eventos: ProductCreated
- Testes obrigatórios:
  - tenant isolation
  - validação de produto
- NFR:
  - latência < 100ms
- Riscos:
  - mistura com Pricing

| Entrega | Critério de pronto |
|---------|-------------------|
| Testes E2E críticos em CI | Fluxo compra completo |
| Testes segregação tenant | Pipeline dedicado |
| Carga mínima (k6 ou equivalente) | p95 dentro do orçamento `06` ou plano de otimização |
| Runbook incidente + on-call rotativo leve | Doc no repositório |

### Fase W1.5 — Buffer (semanas 11–12)

- BC: Buffer
- Dependências: Tenant, IAM
- Eventos: ProductCreated
- Testes obrigatórios:
  - tenant isolation
  - validação de produto
- NFR:
  - latência < 100ms
- Riscos:
  - mistura com Pricing

- Cupom (se ainda não feito), hardening segurança, UX polish, preparação **MVP2** (BC11 contrato real).  

---

## Artefatos obrigatórios ao fim da Wave 1

- OpenAPI publicada (versão 0.x).  
- Diagrama C4 container atualizado.  
- Lista de feature flags e defaults.  
- ADRs iniciais mergeados (`12-adrs-principais.md`).  

---

## Dependências externas

| Dependência | Ação |
|-------------|------|
| Conta PSP sandbox | Semana 6 latest |
| Domínio/DNS para multi-tenant dev | Semana 2 |
| IdP (Keycloak/Cognito) | Semana 3 |

---

## O que **não** entra na Wave 1

- Kafka, microsserviços, SAGA distribuída.  
- Integração Sebrae produtiva.  
- MCP/RAG além de spike opcional fora do caminho crítico.  

---

## Riscos da Wave 1

| Risco | Mitigação |
|-------|-----------|
| Integração PSP atrasada | Mock adapter atrás da mesma porta |
| Acoplamento BCs | Revisões semanais de fronteiras + ArchUnit |
