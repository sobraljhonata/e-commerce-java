# Prompt defaults — Phase 4

Version: 1.1  
Last updated: 2026-05-04  
Status: Active (normative for Phase 4 agents)  
Owner/purpose: Reduzir repetição de instruções nos prompts humanos e estabelecer comportamento padrão dos agentes

## Objetivo

Centralizar regras que **antes eram copiadas em todo o prompt**, para que o utilizador descreva sobretudo **o incremento**, **o BC** e **o comportamento novo**, sem reenumerar governança completa.

## Regra crítica para quem escreve prompts

**Os prompts não precisam repetir** as regras listadas neste ficheiro nem as dos contextos referenciados abaixo, **desde que** a execução use os agentes da pasta `.ai/agents/phase-4-guided-implementation/` (ou instrução explícita de carregar estes contextos).

Os agentes **devem assumir** automaticamente este documento e os contextos indicados na secção **Fontes normativas**.

## Fonte de verdade

- **Decisões de plataforma e implementação** estão nos **ADRs e outputs** em `.ai/outputs/03-architecture/`, em particular o **ADR 16** (`.ai/outputs/03-architecture/16-platform-implementation-standards.md`).
- **Normas operacionais** (testes multi-tenant, checklist de output, estilo, classificação de incrementos) estão nos **contextos** 19–24 em `.ai/context/`.
- **Hierarquia em caso de ambiguidade:** ADR / output arquitetural **>** contexto normativo **>** instruções do agente **>** texto livre do prompt. O prompt **não** revoga ADR sem explícita mudança de documentação.
- **Classificação do incremento** (tipo de trabalho): ver `.ai/context/24-increment-classification.md`; os agentes Phase 4 **inferem** o tipo e aplicam o perfil correspondente.

## Fontes normativas (sempre aplicáveis na Phase 4)

| Tema | Documento |
|------|-----------|
| Decisões de implementação consolidadas | `.ai/outputs/03-architecture/16-platform-implementation-standards.md` |
| Testes e isolamento multi-tenant | `.ai/context/19-multi-tenant-testing-rules.md` |
| Checklist obrigatório de output (tenant-scoped) | `.ai/context/20-multi-tenant-validation-checklist-template.md` |
| Estilo, formatação, commits | `.ai/context/21-code-style-and-formatting-rules.md` |
| Estrutura mínima de entrega | `.ai/context/23-output-template.md` |
| Classificação de incrementos | `.ai/context/24-increment-classification.md` |

## Regra de manutenção

- Alterar **padrões estáveis** de implementação (hexágono, multi-tenant, política 404, pirâmide de testes): atualizar primeiro o **ADR 16** (ou novo ADR em `outputs/03-architecture/`) e só depois ajustar **22/23/24** ou agentes para **remeter** ao novo texto — evitar drift entre ADR e contextos.
- Alterar **só operacional** (ex.: exemplo de comando, redação do template de checklist sem mudar regra): editar o contexto afetado (19–21 ou 20) mantendo coerência com o ADR.
- Novo **tipo de incremento** ou perfil de teste: atualizar **24** e, se necessário, uma linha no agente Phase 4 correspondente; não duplicar tabelas longas em agentes.
- **00-project-context.md** deve manter a secção *Engineering governance bootstrap* alinhada quando entrar ou sair um documento do pacote normativo.
- Qualquer mudança que **contradiga** um ADR existente exige **ADR novo ou revisão explícita** do ADR antigo; não “corrigir” só no prompt.

## Regras padrão (resumo; detalhe nos documentos acima)

### Arquitetura

- Hexagonal por bounded context; domínio puro; use cases só com portas admitidas.

### Multi-tenant

- `tenantId` apenas do JWT/contexto autenticado; nunca como fonte de verdade no request.
- Isolamento comprovado por testes; cross-tenant → **404** onde a política aplicar.

### Validação entre agregados

- Validação por repositório **tenant-aware**; evitar carregar agregados inteiros cruzados no domínio.

### Testes

- Unitário, web, integração e BDD mínimo quando aplicável; cross-tenant obrigatório para operações tenant-scoped aplicáveis; payload com `tenantId` quando Create existir (ver 19).

### Formatação

- Spotless/Prettier/editorconfig conforme 21; sem misturar refactor com formatação massiva.

### Output

- Seguir `.ai/context/23-output-template.md`; checklist multi-tenant com **fail-fast** em `Missing` (ver 20). Classificar o incremento segundo **24**.

## Rastreabilidade Phase 3 / arquitetura

Decisões de alto nível e mapa de BCs continuam em `.ai/outputs/03-architecture/` (ex.: `01-bounded-contexts-oficiais.md`, `03-arquitetura-hexagonal.md`, `04-modelo-multi-tenant.md`). Este ficheiro **não substitui** ADRs anteriores; **alinha** execução da Phase 4 com eles.
