# A framework for building consistent, multi-tenant-ready systems using AI with enforced engineering governance

## 1. Visão geral

Este framework organiza engenharia assistida por IA de forma **governada, auditável e reutilizável**.  
Ele transforma aprendizados de implementações reais em padrões, fluxos e pontos de controle que reduzem deriva de decisão, inconsistência entre incrementos e regressões de arquitetura.

Problema que resolve:

- IA "solta" tende a variar comportamento entre tarefas equivalentes.
- Regras críticas (arquitetura, segurança, isolamento, qualidade) podem ser esquecidas ou aplicadas de forma desigual.
- Entregas sem estrutura dificultam revisão, auditoria e escala entre times.

Para quem é:

- Times de engenharia que usam IA no ciclo de desenvolvimento e revisão.
- Arquitetos e tech leads que precisam de rastreabilidade entre decisão, implementação e validação.
- Organizações com requisitos de governança técnica e consistência operacional.

Diferença prática:

- **IA solta:** respostas ad hoc, baixa previsibilidade, risco de drift.
- **IA governada:** decisões explícitas, execução guiada por contexto, validação padronizada e evidência testável.

## 2. Princípios

- Decisões técnicas centralizadas em artefatos normativos (ex.: ADRs).
- Separação entre **decisão** (arquitetura), **execução** (agentes/use cases) e **validação** (checklists/DoD/self-check).
- Tenant como capacidade transversal quando o domínio for multi-tenant.
- Arquitetura hexagonal como padrão de isolamento entre domínio e infraestrutura.
- Regras de negócio e validação de referência posicionadas no use case (camada de aplicação).
- Consistência de output para facilitar revisão, auditoria e handoff.

## 3. Estrutura do framework

Estrutura alvo (incremental):

- `core/`  
  Conceitos-base do framework (modelo mental, contratos de uso, taxonomias). Pode iniciar vazio.

- `governance/`  
  Ponte para artefatos normativos (incluindo contextos 19–26), sem duplicar conteúdo.

- `agents/`  
  Definições de agentes e papéis operacionais (ex.: execução guiada por fase), mesmo antes de migração completa.

- `adapters/patterns/`  
  Patterns reutilizáveis extraídos de casos reais. Já disponível: aggregate reference validation.

- `templates/`  
  Bootstrap de novos projetos/BCs (prompts, outputs, checklists e pacotes mínimos de adoção).

- `examples/`  
  Casos reais para referência e aprendizado aplicado.

## 4. Patterns disponíveis

- `Aggregate Reference Validation`  
  Descrição: valida referência entre agregados tenant-scoped sem acoplamento direto entre entidades, com política de erro consistente (`404` vs `400`).  
  Quando usar: incrementos de relacionamento/validação entre agregados (ex.: tipo `XA`), especialmente quando a referência exige existência no tenant e regras adicionais de estado.

## 5. Como usar o framework

Fluxo recomendado:

1. Definir contexto do projeto e restrições arquiteturais.
2. Executar implementação/revisão com agentes da Phase 4 (ou equivalentes).
3. Aplicar o padrão de output obrigatório para entregas comparáveis.
4. Selecionar patterns por tipo de incremento (ex.: `XA` -> aggregate reference validation).
5. Validar critérios multi-tenant e qualidade usando checklist normativo.
6. Registrar decisões relevantes e impactos arquiteturais para rastreabilidade.

## 6. Integração com governança existente

Este framework **não substitui** a governança existente; ele a operacionaliza para reuso.

Referências principais:

- ADR 16 (standards de implementação)
- Domain Decisions Log 17
- Contextos 19–26
- Output Template 23
- Increment Classification 24
- Definition of Done 25
- Agent Self-check 26

Diretriz: manter os documentos normativos como fonte de verdade e usar o framework como camada de navegação, aplicação e reuso.

## 7. Roadmap do framework

Próximos passos recomendados:

- Adicionar novos patterns (tenant context enforcement, cross-tenant not found policy, listing/filtering tenant-aware, hardening de payload malicioso).
- Extrair e versionar agentes dentro da estrutura `framework/agents`.
- Criar templates de bootstrap para novos projetos e novos bounded contexts.
- Expandir exemplos para outros domínios além do caso de origem.
- Evoluir para suporte multi-domínio (ex.: logística, turismo, serviços financeiros), preservando núcleo de governança.

## 8. Status

- Versão: `v0.1`
- Maturidade: inicial
- Origem: derivado de projeto real com uso contínuo
- Situação: em evolução incremental, com foco em reutilização segura e previsível
