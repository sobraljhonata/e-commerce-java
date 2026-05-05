# Project Context — Sebrae E-Commerce

## Resumo
Loja virtual desenvolvida para o Sebrae.

## Estado atual da transformação (2026-04-29)
- Discovery, Product e Greenfield concluídos.
- Plataforma alvo: SaaS multi-tenant, monólito modular, hexagonal por BC.
- Stack em implementação: Java 21 + Spring Boot 3.
- JWT é a fonte de contexto autenticado.
- `tenantId` vem exclusivamente do JWT/contexto autenticado para capacidades tenant-scoped.
- BCs já iniciados/entregues na Wave 1 atual: Tenant (W1.1), IAM (W1.2 baseline), Catalog (Product e Category tenant-aware).

## Stack atual
- API: .NET Core 2.1
- Front-end: AngularJS
- Arquitetura: DDD
- Banco: [preencher]
- Auth: [preencher]
- Infra: [preencher]

## Características
- Forte acoplamento ao Sebrae
- Intenção de multi-tenant/SaaS
- Quase todas as entidades ligadas a Empresa
- Produto é a entidade central aparente

## Entidades conhecidas
- Account
- Categorias
- Cliente
- Descontos
- Empresas
- Integração
- Locale
- Loja
- Pedidos
- Produtos
- Social
- Vendas

## Objetivos
1. Extrair o comportamento real do sistema
2. Mapear regras de negócio
3. Transformar isso em documentação de produto
4. Gerar backlog e MVP
5. Avaliar modernização da stack
6. Projetar plataforma moderna e escalável

## Engineering governance bootstrap

Entrypoint de governança para **engenharia assistida por IA** (Phase 4 e trabalho alinhado): o utilizador descreve o **incremento**; regras transversais vivem nos artefactos abaixo.

### Pacote normativo (ler nesta ordem de precedência)

| # | Artefacto | Papel |
|---|------------|--------|
| **16** | `.ai/outputs/03-architecture/16-platform-implementation-standards.md` | **ADR** — padrões de implementação da plataforma (hexágono, multi-tenant, testes, formatação). |
| **19** | `.ai/context/19-multi-tenant-testing-rules.md` | Regras de teste e isolamento multi-tenant. |
| **20** | `.ai/context/20-multi-tenant-validation-checklist-template.md` | Template obrigatório do checklist em entregas tenant-scoped. |
| **21** | `.ai/context/21-code-style-and-formatting-rules.md` | Estilo, formatação, commits. |
| **22** | `.ai/context/22-prompt-defaults.md` | Defaults de prompt e ligação ao pacote acima. |
| **23** | `.ai/context/23-output-template.md` | Estrutura mínima de entrega (9 secções). |
| **24** | `.ai/context/24-increment-classification.md` | Classificação de incrementos e regras por tipo (agentes inferem o tipo). |
| **25** | `.ai/context/25-definition-of-done.md` | Definition of Done geral e por tipo de incremento. |
| **26** | `.ai/context/26-agent-self-check.md` | Auto-check obrigatório de agentes antes de concluir entregas/revisões. |
| **17** | `.ai/outputs/03-architecture/17-domain-decisions-log.md` | Log de decisões de domínio/produto por bounded context. |

*(Os **16** e **17** ficam em `outputs/`; **19–26** são contextos em `.ai/context/`.)*

### Hierarquia de autoridade

1. **ADR / outputs de arquitetura** (ex.: **16**, e documentos em `.ai/outputs/03-architecture/`) — decisões estáveis; prevalecem em conflito.
2. **Contextos normativos** (19–26 e demais em `.ai/context/`) — operacionalizam ADRs e políticas de produto.
3. **Agentes** (`.ai/agents/phase-4-guided-implementation/*.md`) — comportamento e checklist de execução no IDE; **não** redefinem ADR nem contradizem contextos.

### Regra para prompts

**Não é necessário repetir** no prompt as regras já cobertas pelo pacote **16/17 + 19–26** e pelos agentes Phase 4, salvo exceção pontual (ex.: política experimental ainda não documentada). Basta indicar BC, tipo ou descrição do incremento e o resultado esperado; ver `.ai/context/22-prompt-defaults.md`.
