# Agent self-check — Phase 4

Version: 1.0  
Last updated: 2026-05-05  
Status: Active (normative)  
Owner/purpose: Checklist de validação final para agentes antes de concluir implementação/revisão

## Quando aplicar

Antes de finalizar **qualquer** entrega/revisão na Phase 4.

## Checklist obrigatório de auto-validação

1. **Output template 23** usado na estrutura de resposta.
2. **Tipo de incremento** classificado (contexto 24) com justificativa curta.
3. **DoD 25** verificado e status declarado (Done / Done com ressalvas / Not done).
4. **Checklist 20** preenchido quando o incremento for tenant-scoped.
5. **Contexto 21** respeitado (sem mistura indevida de mudança funcional + formatação massiva).
6. **ADR 16** respeitado nas decisões técnicas.
7. **Risco multi-tenant** avaliado explicitamente quando aplicável.
8. **Testes cross-tenant** presentes quando aplicável.
9. Nenhum **Missing** aplicável ignorado sem registro.

## Regra de autocorreção

- Se qualquer item falhar e for corrigível no escopo atual, o agente deve **corrigir antes de finalizar**.
- Se não for possível corrigir no escopo atual, o agente deve:
  - declarar explicitamente a pendência/risco;
  - indicar impacto;
  - não marcar como Approved/Done sem ressalvas.

## Regra de decisão final

- `Missing` em operação aplicável (contexto 20) impede aprovação sem ressalvas.
- Ausência de evidência de isolamento cross-tenant em operação tenant-scoped aplicável deve ser reportada como risco obrigatório.

## Referências

- `.ai/context/20-multi-tenant-validation-checklist-template.md`
- `.ai/context/23-output-template.md`
- `.ai/context/24-increment-classification.md`
- `.ai/context/25-definition-of-done.md`
- `.ai/context/21-code-style-and-formatting-rules.md`
- `.ai/outputs/03-architecture/16-platform-implementation-standards.md`

## Consistência interna

O agente deve validar:

- decisões arquiteturais refletem implementação
- testes cobrem cenários descritos
- checklist corresponde ao que foi implementado
- tipo de incremento é coerente com a solução
