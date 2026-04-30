# Quality Enforcement Roadmap — Agents → CI

Objetivo: registrar evolução de enforcement de qualidade sem alterar pipeline neste momento.

---

## 1. Enforcement atual (já ativo)

### Via agentes / documentação (Phase 4)
- Checklist multi-tenant obrigatório para incrementos tenant-scoped.
- Fail-fast no output quando houver `Missing`.
- Referência normativa obrigatória:
  - `.ai/context/19-multi-tenant-testing-rules.md`
  - `.ai/context/20-multi-tenant-validation-checklist-template.md`
- Guardrail de ArchUnit nos agentes de implementação, arquitetura, TDD/BDD e code review.

### Via testes/código (estado atual)
- ArchUnit em uso para fronteiras arquiteturais.
- Testes unit/web/integração/BDD em BCs já iniciados (Tenant, IAM, Catalog).

---

## 2. Enforcement futuro no CI (planejado, não implementado)

## Gates recomendados

1. **ArchUnit gate**
   - falhar build em violação de camadas/BCs.

2. **Multi-tenant gate**
   - exigir testes cross-tenant em operações tenant-scoped aplicáveis.

3. **BDD gate (escopo mínimo)**
   - rodar suites BDD mínimas dos BCs críticos de Wave 1.

4. **Coverage gate**
   - thresholds mínimos para camadas de domínio e aplicação (por BC).

5. **OpenAPI gate**
   - gerar e validar spec no build para evitar drift de contrato.

6. **Security checks gate**
   - análise de dependências e vulnerabilidades conhecidas.

---

## 3. Ordem sugerida de adoção no CI

1. ArchUnit + security dependency scan
2. multi-tenant tests + coverage mínimo
3. BDD mínimo + OpenAPI contract checks

---

## 4. Critério para promover de “agente-only” para CI

- regra repetida em pelo menos 2 incrementos;
- risco alto em caso de regressão;
- execução estável no tempo de build da equipe.

---

## 5. Rastreabilidade

- ADRs base: `.ai/outputs/03-architecture/12-adrs-principais.md`
- Enforcement de ADRs: `.ai/outputs/03-architecture/13-adr-enforcement-map.md`
- Estado Wave 1: `.ai/outputs/05-guided-implementation/00-wave-1-current-status.md`
