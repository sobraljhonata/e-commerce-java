# ADRs principais (Architecture Decision Records)

**Formato:** Título, Status, Contexto, Decisão, Consequências. **Status:** Aceito (Phase 3 greenfield, 2026-04-07).  

---

## ADR-001 — Greenfield sem reuso de código legado

- **Contexto:** Legado existe como referência de comportamento e riscos.  
- **Decisão:** Nenhum código legado é portado; apenas discovery, testes de caracterização e contratos conceituais informam o novo sistema.  
- **Consequências:** Custo inicial maior; qualidade e segurança superiores; risco de “reinventar errado” mitigado por testes e integração incremental.  

---

## ADR-002 — Monólito modular como padrão inicial

- **Contexto:** NFRs exigem simplicidade operacional no MVP; time ainda calibrando domínio.  
- **Decisão:** Um deployable Spring Boot com módulos por bounded context; extração condicionada a pré-requisitos (`08`, agente 06).  
- **Consequências:** Escalabilidade vertical/horizontal do mesmo artefato; deploy atômico; fronteiras devem ser policiadas por ferramentas.  

---

## ADR-003 — Stack: Java 21, Spring Boot 3, Angular, PostgreSQL, Redis, AWS

- **Contexto:** Comparador de stack (.NET vs Spring) e visão de plataforma moderna.  
- **Decisão:** Adotar Spring/Angular/Postgres/Redis/AWS como padrão; .NET documentado como alternativa corporativa sem mudar desenho DDD.  
- **Consequências:** Curva de aprendizado para times .NET; ecossistema maduro para integração e observabilidade.  

---

## ADR-004 — Multi-tenant: shared DB + `tenant_id` + enforcement na aplicação

- **Contexto:** Legado mostrou risco de vazamento com abordagem implícita.  
- **Decisão:** Coluna obrigatória + filtros automáticos; RLS como endurecimento pós-MVP interno.  
- **Consequências:** Simplicidade de operação; exige disciplina extrema em queries e caches.  

---

## ADR-005 — Arquitetura hexagonal por bounded context

- **Contexto:** Necessidade de trocar PSP, storage e integrações Sebrae sem reescrever núcleo.  
- **Decisão:** Ports & adapters por BC; domínio sem dependência de framework na camada central.  
- **Consequências:** Mais boilerplate inicial; testes mais fáceis e acoplamento reduzido.  

---

## ADR-006 — Eventos: domain events in-process + transactional outbox; sem Kafka na Wave 1

- **Contexto:** Event-driven pode aumentar complexidade operacional cedo.  
- **Decisão:** Outbox em PostgreSQL; consumo via worker leve; Kafka somente se critérios `08` forem atendidos.  
- **Consequências:** Consistência forte local; possível latência maior em fan-out massivo futuro.  

---

## ADR-007 — SAGA distribuída não adotada no MVP

- **Contexto:** Consistência em sistema único não exige coreografia distribuída.  
- **Decisão:** Sagas apenas locais ao monólito entre Order, Payment, Fulfillment.  
- **Consequências:** Menos cenários de compensação parcial exposta na rede; revisão ao extrair serviços.  

---

## ADR-008 — Pagamentos isolados em BC dedicado com PSP externo

- **Contexto:** PCI e múltiplos métodos (PIX, cartão).  
- **Decisão:** BC Payment único ponto de integração; sem dados de cartão brutos; webhooks idempotentes.  
- **Consequências:** Latência dependente de terceiros; necessidade de reconciliação.  

---

## ADR-009 — Design system de plataforma + tenant theming limitado

- **Contexto:** `18` exige foundations antes de páginas complexas.  
- **Decisão:** Pacote `@platform/ui` L0–L4; tenant só sobrescreve subset de tokens aprovados.  
- **Consequências:** Menos liberdade criativa por tenant; maior consistência e segurança.  

---

## ADR-010 — IA (MCP/RAG) fora do core transacional

- **Contexto:** `17` e risco de não-determinismo.  
- **Decisão:** LLM apenas em POC/opcional para backoffice, help e onboarding; proibido para auth e captura de pagamento.  
- **Consequências:** Roadmap de “inovação” paralelo ao core estável.  

---

## ADR-011 — Integrações Sebrae atrás de Integration Hub apenas

- **Contexto:** Acoplamento no legado dificulta evolução multi-cliente.  
- **Decisão:** BC11 é a única fronteira “oficial”; adapters internos por capacidade.  
- **Consequências:** Latência extra hop interno; ganho em testabilidade e substituição.  

---

## Próximos ADRs esperados (não fechados aqui)

- Escolha Material vs PrimeNG.  
- Provedor IdP definitivo.  
- Estratégia exata de deploy (ECS vs EKS).  
