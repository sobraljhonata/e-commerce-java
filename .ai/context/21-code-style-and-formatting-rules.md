# Code Style and Formatting Rules

Version: 1.0
Last updated: 2026-04-30
Status: Active
Owner/purpose: Guardrails de estilo, formatação e line endings para reduzir diffs ruidosos

## Objetivo
Padronizar estilo e formatação sem misturar mudanças funcionais com refactors massivos de whitespace.

## Regras obrigatórias

1. **LF como padrão**
   - `end_of_line = lf` no editor.
   - git normaliza texto com LF.

2. **Formatação em commit separado**
   - Não misturar alteração funcional com formatação massiva.
   - Se houver necessidade de formatar muitos arquivos, criar commit dedicado.

3. **Respeitar formatação existente**
   - Em mudanças pequenas, formatar somente o trecho alterado.
   - Evitar reformatar arquivos inteiros sem necessidade.

4. **Ferramentas padrão do projeto**
   - Backend Java: Spotless.
   - Arquivos web/docs/config (JSON/Markdown/YAML/TS/JS/CSS/SCSS/HTML, quando aplicável): Prettier.

5. **Versionamento de artefatos**
   - `.m2/` não deve ser versionado.
   - `.ai/` deve continuar versionado (contextos, agentes, outputs arquiteturais).

## Validação mínima

- Backend:
  - `mvn -f backend/pom.xml spotless:check`
- (Opcional) aplicar formatação backend:
  - `mvn -f backend/pom.xml spotless:apply`
- Frontend/arquivos gerais (se aplicável):
  - `npx prettier . --check`

## Regras para incrementos tenant-scoped

Este contexto **não substitui** os normativos de multi-tenant:
- `.ai/context/19-multi-tenant-testing-rules.md`
- `.ai/context/20-multi-tenant-validation-checklist-template.md`

Ele apenas adiciona disciplina de estilo/formatação ao fluxo de entrega.
