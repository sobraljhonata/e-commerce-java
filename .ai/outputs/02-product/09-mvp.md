# MVP e roadmap de releases — Fase 2

**Agente:** *MVP Planner* (ajustado pela evidência da Fase 1).  
**Regra:** **nenhuma** escolha de tecnologia neste documento; apenas **recorte de capacidades**.

---

## 1. Hipótese inicial do agente vs ajuste

O agente sugeriu MVP 1 com tenant, loja, catálogo, checkout, pagamento, auth admin e observabilidade mínima.

**Ajuste pela Fase 1 (legado Sebrae):**

- O legado mistura **e-commerce genérico** com **pipelines Sebrae** (cursos, financeiro, cadastro).  
- **MVP 1** abaixo confirma o núcleo **genérico + multi-tenant mínimo viável**, mas trata **integrações Sebrae como camada explícita**: ou **paridade reduzida** com stub/config desligável, ou **slice** acordado com negócio (discovery Q-01/Q-07).
- **Paridade funcional completa** com Sebrae fica majoritariamente em **MVP 2** ou **release de paridade** (ver seção 4).

---

## 2. MVP 1 — “Loja multi-empresa mínima” (confirmado)

**Objetivo de negócio:** Demonstrar que **mais de uma empresa** pode operar com **dados isolados** na vitrine, catálogo, sacola e conclusão de compra **sem** depender da maturidade total das integrações Sebrae.

### Inclui

| Área | Capacidades | Notas |
|------|-------------|--------|
| **Tenant** | Criar/selecionar contexto de empresa em todas as jornadas críticas; **proibir** fallback “primeira empresa” nos fluxos MVP | Substitui RB-010–012 **neste recorte** |
| **IAM** | Login, sessão, perfil básico admin vs cliente; política de autorização **definida** (mesmo que simples) | Corrige risco R-06 como requisito |
| **Storefront** | Home simplificada + navegação para listagem e detalhe | Paridade parcial de US-001–003 |
| **Catálogo** | Produtos com `ETipoProduto`; publicação (`Status`); **sem** obrigatoriedade de integração Sebrae para **tipos simples** (ex.: download ou produto “genérico” acordado) | **Sebrae:** adapter desligado ou mock |
| **Categorias / temas** | Nível mínimo para navegar | Alinhado a Fase 1 |
| **Preço** | Preço por produto na ficha e no checkout | “Pricing básico” |
| **Cliente** | Cadastro PF **ou** PJ (um dos dois se precisar cortar) com `IdEmpresa` explícito | MT |
| **Sacola / pedido** | US-010 core sem vínculo avançado de participantes se necessário para cortar | Pode adicionar na 1.x |
| **Checkout / venda** | Fluxo pedido → pagamento simulado ou **registro manual de pagamento confirmado** acordado com negócio | **Sem** obrigar RB-009 no MVP 1 |
| **Pós-compra mínimo** | Lista de vendas do cliente | US-030 reduzido |
| **Observabilidade mínima** | Indicador de falhas de checkout e de integração **se integração existir** | US-072 reduzido — definição qualitativa |

### Explicitamente **fora** do MVP 1

- Inscrição paga com integração financeira Sebrae (RB-009).
- Disponibilidade de curso dependente de `EventoCurso` (RB-003) — **a menos que** negócio declare bloqueante (então MVP 1 vira “MVP Sebrae” — ver discovery).
- Lista de desejos, lead, consultoria, sync evento, relatórios avançados.
- Login social (pode entrar como MVP 1.1 se for crítico — **discovery** Q-02).

### Riscos do recorte MVP 1

| Risco | Tratamento |
|-------|------------|
| MVP não representa o valor atual para Sebrae | Comunicar como “plataforma base”; paralelamente planejar MVP 2 de integração |
| Pagamento real ausente | Aceitar “pagamento registrado” operacionalmente ou integração mínima **definida em workshop** (sem tecnologia aqui) |

---

## 3. MVP 2 — “Paridade operacional + integrações Sebrae”

**Objetivo:** Aproximar-se da **paridade** com o legado nas áreas de maior receita e compliance para o ecossistema Sebrae.

### Inclui (prioridade sugerida)

1. **Catálogo cursos/eventos** com RB-003 (integração ou adapter oficial).
2. **Checkout** completo com termos (US-013) e **inscrição paga** (RB-009) atrás de **configuração parametrizável** (US-071).
3. **Cadastro PF/PJ** com consultas CPF/CNPJ (UC-12).
4. **Reserva/inscrição/cancelamento** após fechar discovery Q-02/Q-05.
5. **Cupom** e **campanhas** (escopo F-PRM-02 detalhado).
6. **Lista de desejos**, **lead** com roteamento MT (US-041).
7. **Consultoria** (US-050) com correção RB-010.
8. **Sync evento** (US-004).
9. **Dashboard/relatórios** (US-060–061) conforme inventário.
10. **Conteúdo institucional** completo (US-040).
11. **Login social** se confirmado como paridade.

### Modernização de produto dentro do MVP 2 (sem tech)

- Templates de e-mail e copy **parametrizáveis** (reduz SEB em UX).
- Mensagens de falha de integração **acionáveis**.

---

## 4. Pós-MVP (horizonte)

| Faixa | Conteúdo |
|-------|----------|
| **Release Paridade+** | Tudo em `05-backlog-features.md` marcado apenas PARC ainda pendente |
| **V3 Sebrae** | Anti-corrupção formal, contratos versionados, feature flags por empresa |
| **V4 MT avançado** | Sharding, políticas por plano, white-label completo |
| **Pagamentos modernos** | PIX, link de pagamento, checkout transparente (objetivo em `.ai/context/02-modernization-goals.md`) |
| **Confiabilidade** | SLAs, retries idempotentes, dead-letter **como produto** |
| **Substituição de batch** | Se `winService` for confirmado — UC-D1 |

---

## 5. Itens fora de escopo (explícito)

- Definir arquitetura alvo (monólito modular vs microsserviços) — ver `.ai/context/02-modernization-goals.md` regra de decisão.
- Migração de dados detalhada (será fase própria).
- UX redesign completo **fora** do mínimo necessário para MT e clareza.
- **Reescrever** todas as integrações Sebrae no MVP 1 — apenas **interfaces** e acordos.
- Gamificação, marketplace entre empresas, B2B complexo — **não** evidenciados na Fase 1.

---

## 6. Visões obrigatórias × releases

| Visão | MVP 1 | MVP 2 | Pós-MVP |
|-------|-------|-------|---------|
| **Paridade** | Parcial (núcleo loja) | Alta | Fechamento de gaps |
| **Modernização** | Mínima (auth clara, erros legíveis) | Média (templates, observabilidade) | Alta (pagamentos modernos) |
| **Sebrae** | Opcional/mock | Central | Desacoplamento progressivo |
| **Multi-tenant** | **Obrigatório** | Reforço (lead, consultoria, CMS) | Plano/políticas avançadas |

---

## 7. Métricas de sucesso sugeridas (negócio)

| Métrica | MVP 1 | MVP 2 |
|---------|-------|-------|
| Empresas operando em paralelo sem vazamento | ≥ 2 piloto | Produção |
| Jornada compra concluída | ≥ 1 fluxo ponta a ponta | Fluxos por tipo de produto acordados |
| Dependência de constantes financeiras em código | Zero novas | Zero legado |
| Tempo para diagnosticar falha de checkout | Definido workshop | Reduzido vs baseline legado |

---

## 8. Referências cruzadas

- Épicos: `04-backlog-epicos.md`
- Features: `05-backlog-features.md`
- Histórias: `06-backlog-historias.md`
- Tasks: `07-backlog-tasks.md`
- Riscos: `08-riscos-dependencias-e-duvidas.md`
