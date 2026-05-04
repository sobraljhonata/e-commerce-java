# ADR — Platform implementation standards

## Status

Accepted

## Context

O projeto é um SaaS multi-tenant (e-commerce) em **monólito modular**, com **arquitetura hexagonal por bounded context** (Java 21, Spring Boot 3). O **tenant** é obtido via **JWT** (`tenantId` no contexto autenticado). Agentes e prompts humanos repetiam as mesmas regras críticas, aumentando risco de inconsistência entre incrementos.

Este ADR complementa o material já existente em `13-adr-enforcement-map.md`, `12-adrs-principais.md` e na Phase 3 em `.ai/outputs/03-architecture/`. Foi numerado **16** para evitar colisão com o ficheiro **13** já presente nesta pasta.

## Decision

As regras abaixo são **normativas** para implementação na Wave atual e fases guiadas por agentes. Detalhamento operacional de testes e checklist mantém-se em `.ai/context/19-multi-tenant-testing-rules.md` e `.ai/context/20-multi-tenant-validation-checklist-template.md`; formatação em `.ai/context/21-code-style-and-formatting-rules.md`.

### Arquitetura

- **Hexagonal por bounded context**: domínio e casos de uso no núcleo; adapters para HTTP, persistência e integrações.
- **Domínio** não depende de Spring, de `adapters` nem de infraestrutura.
- **Casos de uso** dependem apenas de **portas** (interfaces na aplicação) e de abstrações de plataforma explicitamente admitidas (ex.: contexto de utilizador autenticado).

### Multi-tenant

- **`tenantId`** vem **exclusivamente** do contexto autenticado (JWT validado), não do request.
- **Proibição** de usar `tenantId` em body, query, path ou header como fonte de verdade.
- **Política de resposta**: recurso inexistente **ou** de outro tenant → **404** (evitar 403 que facilite enumeração), salvo ADR específico que defina outra política.
- **Payload** com `tenantId` extra: deve haver política explícita no BC (ignorar vs 400); ver contexto 19.

### Validação entre agregados

- Referências entre agregados (ex.: produto → categoria) validam-se com **portas repositório tenant-aware** (`findByIdAndTenant`, etc.), não por carregar o agregado completo cruzado no domínio.
- O domínio pode guardar apenas identificadores (ex.: `categoryId`); regra de existência no tenant fica na aplicação.

### API / DTOs

- **DTOs de escrita** não expõem `tenantId` como campo aceite pelo contrato público.
- Erros de categoria/recurso fora do tenant seguem a mesma política de **404** onde aplicável.

### Testes

- Pirâmide mínima alinhada ao incremento: **unitário**, **web** (adapter HTTP isolado quando existir), **integração** (stack realista), **BDD** quando o fluxo for comportamento principal.
- **Cross-tenant**: obrigatório para operações tenant-scoped aplicáveis (ver contexto 19).
- **Payload malicioso** com `tenantId`: obrigatório quando a operação Create existir e aceitar JSON (ver contexto 19).

### Formatação e commits

- Respeitar `.editorconfig`, `.gitattributes`, **Spotless** (backend) e **Prettier** onde aplicável (ver contexto 21).
- **Não misturar** alteração funcional com **formatação massiva**; formatação alargada → commit dedicado.

## Consequences

- Prompts podem focar no **delta** do incremento; regras estáveis passam a `.ai/context/22-prompt-defaults.md` e a este ADR.
- Revisões devem validar aderência a este ADR e aos contextos 19–21.
- Novos BCs ou exceções exigem **atualização explícita** deste ADR ou ADR filho, em vez de convenções só na conversa.

## References

- `.ai/context/19-multi-tenant-testing-rules.md`
- `.ai/context/20-multi-tenant-validation-checklist-template.md`
- `.ai/context/21-code-style-and-formatting-rules.md`
- `.ai/context/22-prompt-defaults.md`
- `.ai/context/23-output-template.md`
- `.ai/outputs/03-architecture/13-adr-enforcement-map.md`
