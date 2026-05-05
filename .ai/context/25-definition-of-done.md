# Definition of Done — Phase 4

Version: 1.0  
Last updated: 2026-05-05  
Status: Active (normative)  
Owner/purpose: Definir critérios mínimos de pronto por tipo de incremento para evitar conclusão prematura

## Regra geral de Done

- Um incremento só pode ser marcado como **Done** quando **todos os itens obrigatórios aplicáveis** estiverem atendidos.
- Se qualquer item obrigatório aplicável falhar, o status deve ser **Not done** ou **Done com ressalvas** (com pendências explícitas).
- Para incrementos tenant-scoped, a seção `## Multi-tenant validation checklist` (contexto 20) é mandatória no output.

## DoD geral — Phase 4 (todos os tipos)

1. Tipo de incremento classificado conforme `.ai/context/24-increment-classification.md`.
2. Entrega estruturada conforme `.ai/context/23-output-template.md`.
3. Aderência ao ADR técnico vigente (`.ai/outputs/03-architecture/16-platform-implementation-standards.md`).
4. Regras de formatação e separação de commits conforme `.ai/context/21-code-style-and-formatting-rules.md`.
5. Testes executados no nível aplicável ao incremento (ou justificativa explícita de N/A).
6. Pendências/riscos declarados quando houver.

## DoD por tipo de incremento

### TS — Tenant-scoped CRUD/feature

- Tenant vem exclusivamente do contexto autenticado/JWT.
- `tenantId` não é fonte de verdade em body/query/path/header.
- Operações aplicáveis cobertas com testes de isolamento (incluindo cross-tenant).
- Política de 404 para recurso inexistente ou de outro tenant validada quando aplicável.
- Checklist do contexto 20 preenchido sem `Missing` em operação aplicável.

### XA — Aggregate relationship (cross-aggregate validado)

- Tudo de TS, além de:
- Referência entre agregados validada via porta tenant-aware (ex.: `findByIdAndTenant`).
- Agregado não carrega agregado externo inteiro quando bastar ID + validação.
- Testes para referência inexistente e referência de outro tenant.

### RF — Query/filtering/listing

- Filtros aplicados sem quebrar isolamento por tenant.
- Lista vazia com contrato HTTP correto (`200 []`, quando essa for a política do endpoint).
- Teste de listagem filtrada + cenário cross-tenant aplicável.
- Sem regressão da listagem sem filtro.

### CR — Code refactor

- Sem alteração de comportamento observável.
- Mudança funcional não deve ser misturada no mesmo lote.
- Testes existentes continuam a passar.
- Se tocar fluxo tenant-scoped, checklist 20 deve ser revisto (N/A apenas com justificativa).

### Security / IAM

- Contexto autenticado consistente (claims obrigatórias e validação).
- Controles de autorização da rota/caso de uso cobertos por testes.
- Ausência de bypass por parâmetros de request para contexto de tenant.
- Erros de autenticação/autorização com contrato explícito.

### Documentation / governance (DC)

- Alterações restritas a docs/artefatos de governança.
- Rastreabilidade explícita entre ADR, contextos e agentes.
- Sem conflito com ADR 16; sem duplicação desnecessária.
- Template de output seguido (com N/A justificado para secções técnicas).

## Referências

- `.ai/context/20-multi-tenant-validation-checklist-template.md`
- `.ai/context/21-code-style-and-formatting-rules.md`
- `.ai/context/23-output-template.md`
- `.ai/context/24-increment-classification.md`
- `.ai/outputs/03-architecture/16-platform-implementation-standards.md`
