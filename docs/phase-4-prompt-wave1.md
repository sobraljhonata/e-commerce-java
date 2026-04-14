Use os agentes da pasta `.ai/agents/phase-4-guided-implementation` e os outputs das fases anteriores.

Quero iniciar a Wave 1 da nova plataforma.

Ordem obrigatória:
1. `01-implementation-orchestrator.md`
2. `02-architecture-tutor.md`
3. `06-tdd-bdd-tutor.md`
4. Se houver UI, `03-design-system-tutor.md`
5. Fechar com `07-code-review-and-refactoring-tutor.md`

Escopo desta Wave 1:
- W1.0 Platform Skeleton
- W1.1 Tenant Management
- W1.2 IAM baseline
- W1.3 Tenant admin shell
- W1.4 Catalog seed

Regras:
- respeitar os bounded contexts oficiais
- respeitar os ADRs
- usar arquitetura hexagonal
- aplicar multi-tenant explícito
- começar pelo menor incremento funcional possível
- explicar decisão, trade-offs, cenários BDD e estratégia TDD

Quero que cada incremento me entregue:
1. objetivo de negócio
2. BC envolvido
3. decisão arquitetural
4. cenários BDD
5. primeiro teste TDD
6. código sugerido
7. revisão crítica
