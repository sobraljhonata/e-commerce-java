# Output template — Phase 4

Version: 1.1  
Last updated: 2026-05-04  
Status: Active (normative template)  
Owner/purpose: Estrutura mínima obrigatória de entrega para implementação/revisão na Phase 4

## Uso

Toda **entrega** de incremento (implementação ou revisão concludente) produzida pelos agentes da Phase 4 deve incluir as secções **1 a 9** abaixo, nesta ordem. Conteúdo desnecessário à entrega pode ser omitido com **uma frase** (ex.: "N/A — incremento sem superfície HTTP"), mas a **secção deve existir**.

Para incrementos **tenant-scoped**, a secção **8** é obrigatória com a tabela no formato literal de `.ai/context/20-multi-tenant-validation-checklist-template.md`.

## Fail fast

- Se existir **Missing** numa operação **aplicável** do checklist (secção 8), o output deve **destacar** o facto, **não** aprovar sem ressalvas e indicar correções pendentes.

---

## 1. Objetivo

O que o incremento pretende entregar (1–3 frases objetivas) e o **tipo classificado** (códigos em `.ai/context/24-increment-classification.md`) com **uma frase** de justificativa da inferência.

## 2. Decisões arquiteturais

Decisões relevantes (BC, portas, limites, trade-offs). Referenciar ADR ou output em `.ai/outputs/03-architecture/` quando aplicável.

## 3. Sequência TDD

Ordem de testes/código (Red → Green → Refactor ou equivalente) ou justificativa curta se N/A.

## 4. Implementação

Resumo do que foi feito (comportamento, contratos HTTP, casos de uso). Evitar colar listagens enormes de código.

## 5. Arquivos criados/alterados

Lista ou tabela: caminhos principais; marcar **criado** vs **alterado**.

## 6. Testes executados

Comandos ou suites (ex.: `mvn test`, suíte BDD). Resultado esperado: sucesso; se falhou, descrever e corrigir antes de concluir.

## 7. Validação manual

Passos curtos para validar em ambiente local (curl/UI), ou "N/A" com razão.

## 8. Multi-tenant validation checklist

Copiar a tabela do template **literal** de `.ai/context/20-multi-tenant-validation-checklist-template.md` (mesmas colunas: Operação, Regra obrigatória, Teste/evidência, Status, Decisão).  
Operações não aplicáveis: **N/A** com justificativa.  
**Não** deixar `Missing` sem plano de correção em operações aplicáveis.

## 9. Observações arquiteturais

Dívida técnica, follow-ups, riscos residuais, notas para o próximo incremento.
