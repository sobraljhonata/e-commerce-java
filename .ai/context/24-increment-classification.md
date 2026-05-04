# Increment classification — Phase 4

Version: 1.0  
Last updated: 2026-05-04  
Status: Active (normative for Phase 4 agents)  
Owner/purpose: Classificar incrementos para aplicar o conjunto mínimo certo de regras, testes e formato de entrega sem repetir prompts

## Instruções para agentes

1. **Inferir** o tipo a partir do pedido (BC, verbos HTTP, “só doc”, “refactor”, etc.).
2. Se **ambíguo**, fazer **no máximo uma** pergunta de clarificação **ou** assumir o perfil **mais restritivo** plausível (preferir **TS** quando houver dados por tenant).
3. Aplicar a coluna **Regras / evidências** do tipo escolhido; remeter a **19–21** e **ADR 16** em vez de copiar texto longo.
4. Declarar explicitamente no output (template **23**, secção 1 ou 2): **tipo inferido** e **justificativa em uma frase**.

## Tipos de incremento

| Código | Nome | Quando usar |
|--------|------|----------------|
| **TS** | Tenant-scoped feature | API ou domínio que lê/escreve dados **escopados a tenant** (JWT). |
| **XA** | Cross-aggregate / referência validada | Altera vínculo ou validação entre agregados **no mesmo BC** (ex.: FK + `findByIdAndTenant`). |
| **NT** | Non-tenant técnico | Mudança **sem** semântica multi-tenant (ex.: util compartilhado, config global explicitamente fora de tenant). |
| **RF** | Refactor-only | Comportamento observável **inalterado**; só estrutura/nomes. |
| **BF** | Bugfix | Corrige comportamento errado **sem** novo contrato; escopo mínimo. |
| **DS** | Design system / UI | Tokens, componentes, acessibilidade; pode combinar com **TS** se a UI for tenant-aware. |
| **DC** | Docs / governança | Apenas `.ai/`, ADRs, README de arquitetura — **sem** código de produção. |

## Regras por tipo

### TS — Tenant-scoped feature

- Aplicar **ADR 16** + **19** + checklist **20** na entrega (secção 8 do **23**).
- Testes: unit + web + integração quando houver HTTP; **cross-tenant** obrigatório para operações aplicáveis; **payload `tenantId`** quando existir Create com JSON (19).
- **Fail-fast** em `Missing` no checklist (20).

### XA — Cross-aggregate

- Tudo de **TS**, mais: validação **só** via portas **tenant-aware**; domínio sem carregar agregado alheiro completo (16).
- Testes: casos **categoria/recurso inexistente** e **outro tenant** para a referência nova ou alterada.

### NT — Non-tenant técnico

- Checklist 20: marcar **N/A** com justificativa por linha não aplicável; ainda assim **23** completo.
- Testes no nível adequado ao risco (não forçar BDD se não houver comportamento de negócio).

### RF — Refactor-only

- Proibir mistura com mudança funcional no mesmo lote (21); testes existentes devem continuar a passar.
- Checklist 20: **N/A** global salvo se tocar código que já era tenant-scoped (avaliar caso a caso).

### BF — Bugfix

- Teste de **regressão** mínimo no ponto quebrado; se o bug for multi-tenant, aplicar **TS** nas operações afetadas.

### DS — Design system / UI

- **21** (Prettier, etc.); se ecrãs forem tenant-scoped, checklist **20** nas partes que consumirem dados por tenant.
- BDD opcional conforme impacto.

### DC — Docs / governança

- Sem alteração a `src/` de produção ou testes; **23** pode usar “N/A” em secções 3–7 com justificativa.

## Relação com outros contextos

- Hierarquia e pacote normativo: **00-project-context.md** (*Engineering governance bootstrap*) e **22-prompt-defaults.md** (*Fonte de verdade*).
- Este ficheiro **não** altera políticas já fixadas em **16**, **19** ou **20**; apenas **roteia** esforço de teste e de checklist.
