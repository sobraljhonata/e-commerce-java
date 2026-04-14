# Sebrae E-Commerce Agent Pack — Opinionated Edition

Versão opinativa preparada para:
- engenharia reversa profunda do legado;
- reconstrução do produto;
- comparação de caminhos tecnológicos;
- arquitetura alvo moderna;
- migração incremental;
- reescrita guiada com o agente atuando também como tutor.

## O que esta edição adiciona
Além da estrutura anterior, esta edição inclui:
- hipótese inicial de bounded contexts;
- visão opinativa de MVP;
- posição inicial sobre modular monolith vs microservices;
- agentes tutores para a fase de implementação;
- trilha prática para aprender enquanto reescreve:
  - Design System
  - Software Architecture
  - Distributed Systems
  - SAGA
  - TDD
  - BDD

## Tese inicial desta edição
Para este e-commerce, o ponto de partida mais seguro tende a ser:
1. **discovery profunda do legado**
2. **modular monolith moderno na fase 1**
3. **extração posterior de serviços por necessidade real**
4. **eventos e SAGA só onde o processo de negócio justificar**
5. **multi-tenant desenhado como capacidade de plataforma, não como detalhe de tabela**
6. **design system como ativo de produto, não apenas biblioteca de componentes**

## Estrutura

```txt
.ai/
  context/
    00-project-context.md
    01-domain-map.md
    02-modernization-goals.md
    03-constraints.md
    04-target-options.md
    05-opinionated-architecture-baseline.md
    06-learning-goals.md
  agents/
    phase-1-discovery/
      01-legacy-reverse-engineer.md
      02-business-rules-miner.md
      03-flow-mapper.md
      04-sebrae-decoupling-analyst.md
      05-tenant-model-analyst.md
      06-discovery-consolidator.md
    phase-2-product/
      01-product-reconstructor.md
      02-backlog-builder.md
      03-mvp-planner.md
    phase-3-modernization/
      01-solution-architect.md
      02-microservices-and-saga-architect.md
      03-platform-architect.md
      04-target-stack-comparator.md
      05-migration-strategist.md
      06-test-and-quality-strategist.md
    phase-4-guided-implementation/
      01-implementation-orchestrator.md
      02-architecture-tutor.md
      03-design-system-tutor.md
      04-distributed-systems-tutor.md
      05-saga-tutor.md
      06-tdd-bdd-tutor.md
      07-code-review-and-refactoring-tutor.md
  templates/
    rule-template.md
    use-case-template.md
    epic-template.md
    feature-template.md
    story-template.md
    task-template.md
    architecture-decision-record.md
    bounded-context-template.md
    implementation-learning-loop.md
  outputs/
    01-discovery/
    02-product/
    03-architecture/
    04-migration/
    05-guided-implementation/
```

## Sequência recomendada
1. Discovery
2. Produto
3. Arquitetura alvo
4. Migração
5. Implementação guiada por tutoria

## Postura desta edição
Os agentes não devem apenas responder “o que fazer”.
Eles devem também explicar:
- por que fazer;
- por que não fazer outra coisa;
- quais trade-offs existem;
- o que você está aprendendo ao implementar.
