Use os agentes da pasta `.ai/agents/phase-3-modernization`, todos os contextos em `.ai/context`, e especialmente os contextos que redefinem a Phase 3 como greenfield.

Agentes prioritários:
- `01-solution-architect.md`
- `02-microservices-and-saga-architect.md`
- `03-platform-architect.md`
- `04-target-stack-comparator.md`
- `05-migration-strategist.md`
- `06-test-and-quality-strategist.md`
- `07-greenfield-platform-architect.md`
- `08-nfr-and-reliability-architect.md`
- `09-ai-augmentation-architect.md`

Entradas obrigatórias:
- `.ai/outputs/01-discovery/`
- `.ai/outputs/02-product/`
- `.ai/outputs/03-strategic-platform-audit/`

Quero executar uma Phase 3 profunda, com foco em desenhar uma nova plataforma comercial digital moderna, sem reaproveitar código legado.

Premissas obrigatórias:
- não vamos reutilizar nada do legado;
- o legado é apenas fonte de discovery e validação de capacidades;
- queremos uma plataforma de e-commerce / comércio digital moderna, escalável, modular, multi-tenant e multi-cliente;
- queremos pensar desde já em baixa latência, alta disponibilidade, arquitetura hexagonal, design system, segurança, observabilidade e custo operacional;
- queremos avaliar o uso de MCP e RAG onde fizer sentido para uma POC ou capability estratégica;
- queremos tempo para consolidar um produto inovador, e não apenas modernizar tecnologia.

Quero que a Phase 3 feche:
1. North Star da plataforma
2. bounded contexts oficiais
3. stack alvo e estilo arquitetural
4. arquitetura hexagonal do backend
5. estratégia de multi-tenant
6. estratégia de design system
7. estratégia de segurança, observabilidade, latência e alta disponibilidade
8. estratégia de pagamentos
9. decisão sobre eventos, Kafka e SAGA
10. estratégia para MCP e RAG
11. MVP da plataforma
12. blueprint detalhado da Wave 1
13. ADRs principais

Regras:
- não herdar limitações do legado sem justificativa;
- não assumir microsserviços como padrão;
- não assumir Kafka ou SAGA como obrigatórios;
- não colocar IA no caminho crítico transacional sem justificativa forte;
- separar claramente:
  - fato herdado do discovery
  - decisão nova de arquitetura
  - hipótese estratégica
  - risco
  - recomendação

Quero a saída organizada em documentos em `.ai/outputs/03-architecture/`.

Arquivos esperados:
- `00-north-star-da-plataforma.md`
- `01-bounded-contexts-oficiais.md`
- `02-stack-e-estilo-arquitetural.md`
- `03-arquitetura-hexagonal.md`
- `04-modelo-multi-tenant.md`
- `05-design-system-strategy.md`
- `06-security-observability-reliability.md`
- `07-payment-architecture.md`
- `08-events-kafka-saga-decision.md`
- `09-mcp-rag-strategy.md`
- `10-mvp-da-plataforma.md`
- `11-wave-1-blueprint.md`
- `12-adrs-principais.md`

Importante:
A resposta deve buscar fechamento de decisão, não apenas exploração conceitual.
