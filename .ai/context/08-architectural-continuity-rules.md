# Architectural Continuity Rules

## Objetivo
Garantir consistência entre discovery, backlog, arquitetura e implementação.

## Regras
- O sistema deve ser pensado por capability e bounded context, não por camada técnica isolada.
- O rewrite deve ocorrer por fluxo de negócio e capability, não por controller aleatório.
- O modular monolith é a hipótese inicial padrão.
- Extração de microsserviços exige justificativa explícita.
- Introdução de Kafka exige justificativa explícita.
- Introdução de SAGA exige justificativa explícita.
- Multi-tenant deve ser tratado como capacidade transversal de plataforma.
- Design System deve começar por foundations:
  - tokens
  - primitives
  - componentes base
  - padrões
- TDD deve orientar a implementação do domínio.
- BDD deve orientar a descoberta e os cenários de aceitação.
- Integrações legadas devem ser isoladas em adapters ou contextos específicos.
- Acoplamentos Sebrae devem ser removidos, parametrizados ou isolados conscientemente.