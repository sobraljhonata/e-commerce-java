Use os agentes:
- `.ai/agents/phase-2-product/04-ecommerce-strategy-auditor.md`
- `.ai/agents/phase-3-modernization/07-platform-development-planner.md`

E também todos os contextos em `.ai/context/`, especialmente:
- `11-ecommerce-platform-principles.md`

Entradas obrigatórias:
- `.ai/outputs/01-discovery/`
- `.ai/outputs/02-product/`

Quero uma análise estratégica completa deste e-commerce legado com foco em transformá-lo em uma plataforma:
- escalável
- multi-produto
- multi-cliente
- multi-tenant
- modularizada

Objetivos:
1. analisar discovery e product;
2. revisar criticamente se os fluxos encontrados fazem sentido para uma plataforma de e-commerce;
3. identificar falhas, fragilidades, lacunas, riscos e alertas de segurança;
4. apontar riscos de segregação entre tenants e clientes;
5. identificar o que hoje é acoplamento legado e o que é capacidade real do produto;
6. propor melhorias para transformar a solução em plataforma;
7. estruturar grupos de módulos por famílias de produto quando fizer sentido;
8. gerar um plano de desenvolvimento do sistema.

Quero que a resposta venha organizada em:
1. Diagnóstico executivo
2. Auditoria de capacidades
3. Auditoria de fluxos
4. Auditoria de segurança e segregação
5. Oportunidades de melhoria
6. Estrutura alvo da plataforma
7. Plano de desenvolvimento por fases
8. MVP da plataforma
9. Pós-MVP
10. Alertas críticos e próximos passos

Regras:
- não presumir que o sistema já é uma plataforma;
- não presumir que multi-tenant já está resolvido;
- não presumir que o fluxo de compra está completo e seguro;
- sempre separar fato confirmado, hipótese e recomendação;
- priorizar visão de negócio + produto + arquitetura + segurança;
- quando necessário, indicar o que precisa de validação manual no legado em execução.
