# Auditoria estratégica profunda — legado e produto reconstruído

**Papel:** especialista em e-commerce, plataforma de loja, SaaS multi-tenant/multi-cliente, produto digital, segurança de aplicação e arquitetura escalável.  
**Agentes:** `.ai/agents/phase-2-product/04-ecommerce-strategy-auditor.md`, `.ai/agents/phase-3-modernization/07-platform-development-planner.md`  
**Contextos:** `.ai/context/*` — ênfase em `11`, `12`, `13`, `14`  
**Entradas obrigatórias:** `.ai/outputs/01-discovery/`, `.ai/outputs/02-product/`  
**Data:** 2026-04-07  

### Legenda de evidência (`13-validation-and-testing-needs.md`)

| Tag | Significado |
|-----|-------------|
| **[Código]** | Confirmado por código / artefato estático |
| **[Fluxo]** | Confirmado por encadeamento front + back + regras documentadas |
| **[Hipótese]** | Evidência parcial — não elevar a “verdade de arquitetura” sem validar |
| **[Teste manual]** | Requer teste manual no sistema em execução |
| **[Negócio]** | Requer validação com negócio |
| **[Segurança]** | Requer teste de segurança |
| **[Segregação]** | Requer teste de segregação entre tenants |

### Legenda de classificação (`12-platform-capability-classification.md`)

**CP** = Core de plataforma · **CT** = Core transacional · **SUP** = Supporting · **COM** = Commodity · **LEG** = Legado / acoplamento específico · **DESC** = Candidata à descontinuação (com critério)

---

## 1. Diagnóstico executivo

### 1.1 Síntese

| Pergunta | Resposta (rigorosa) |
|----------|---------------------|
| **Maturidade do e-commerce** | **Fato [Código][Fluxo]:** existe jornada completa documentada (vitrine → detalhe → sacola → checkout → venda → pós-compra) com regras em `MaisCode.App` (RB-001 a RB-013). **Hipótese:** atomicidade e resiliência do checkout não estão provadas sem testes integrados (RB-007). |
| **Maturidade SaaS / plataforma** | **Fato [Código]:** `IdEmpresa` proliferado no domínio; existem atalhos `GetAll().First()` / `FirstOrDefault()` para empresa em lead, consultoria, cliente (RB-010–012, discovery `06-analise-multi-tenant.md`). **Conclusão:** **não** tratar como plataforma multi-tenant madura — é **schema multi-empresa** com **comportamento single-tenant** em pontos críticos. **[Segregação]** obrigatório antes de escalar clientes. |
| **Principais riscos** | (1) Vazamento lógico entre tenants e notificações ao tenant errado **[Código]** **[Segregação]**; (2) superfície API permissiva (CORS `*`, OAuth `AllowInsecureHttp`, exemplos de `[Authorize]` comentado) **[Código]** **[Segurança]**; (3) acoplamento Sebrae em catálogo, financeiro e cadastro **[Código][Fluxo]**; (4) hardcodes financeiros (RB-009) **[Código]**. |
| **Principais oportunidades** | Modularizar por **capability** (`11`, `14`); isolar integrações em **adapters**; usar `ETipoProduto` como gancho para **famílias de produto**; MVP focado em **compra segura + tenant real** (`11` último princípio). |

---

## 2. Maturidade atual do sistema

### 2.1 Eixo transacional

| Área | Avaliação | Evidência |
|------|-----------|-----------|
| Catálogo e vitrine | **Razoável a forte** para o caso Sebrae/educação | RB-001–003, fluxos F1–F3 **[Fluxo]** |
| Pedido / sacola | **Razoável** | RB-004, RB-005 **[Código]** |
| Promoção (cupom) | **Razoável**; vínculo cupom×carrinho não fechado na discovery | RB-006 **[Código]**; gap **[Hipótese][Teste manual]** |
| Venda / checkout | **Frágil para “confiável em escala”** | RB-007, RB-009 **[Código]** |
| Pagamento adquirente | **Indeterminado como produto fechado** | Projeto `MaisCode.Pagamento` **[Código]**; encadeamento E2E **[Teste manual][Negócio]** |

### 2.2 Eixo técnico e operação

| Área | Avaliação | Evidência |
|------|-----------|-----------|
| API e front | **Razoável** pós-restore Git | Controllers + Angular documentados discovery |
| Integrações Sebrae | **Implementadas mas acopladas** | `Metodos/*.cs`, RB-003, RB-009 **[Código]** |
| Observabilidade de negócio | **Fraca como capability** | Não evidenciado como produto na discovery **[Hipótese][Negócio]** |
| Confiabilidade como **base** para reescrita | **Parcial** | Comportamento útil **[Fluxo]**; riscos MT/segurança impedem “congelar como spec” sem auditoria **[Segregação][Segurança]** |

---

## 3. Maturidade da visão SaaS / multi-tenant / plataforma

### 3.1 Perguntas diretas (negócio e produto)

| Pergunta | Resposta |
|----------|----------|
| **Que tipo de e-commerce é hoje?** | **Fato:** e-commerce **B2C verticalizado** com forte componente **educação/evento/download/consultoria** e integrações **Sebrae** — não é retail genérico neutro. |
| **Produto reutilizável ou projeto custom?** | **Hipótese forte [Negócio]:** origem próxima de **projeto/customização** para um ecossistema; **evidência de reuso:** modelo `Empresa` + múltiplos módulos. **Conclusão prudente:** **aparência parcial de plataforma**, **comportamento não garantido** para N clientes heterogêneos. |
| **Capacidades que geram valor** | Catálogo multi-tipo, checkout com termos/inscrição onde aplicável, integração cadastral e financeira **no contexto Sebrae** — **Fato [Fluxo]**. |
| **Commodity** | Login básico, FAQ/HTML, upload, logging implícito — **SUP/COM** (`12`). |
| **Criadas “só para Sebrae”?** | Disponibilidade curso via `EventoCurso`, financeiro `postVendaEcommerce`, campos no `Produto`, consultas CPF/CNPJ — **Fato [Código]** = **LEG** com alto valor **no** segmento atual. |
| **Múltiplos clientes “de verdade”?** | **Fato [Código]:** múltiplas **linhas de empresa** possíveis no BD; **comportamento** pode colapsar para “primeira empresa” — **não** equivale a multi-cliente seguro até prova **[Segregação]**. |

### 3.2 Perguntas de plataforma (`11-ecommerce-platform-principles.md`)

| Princípio | Situação legado |
|-----------|-----------------|
| Tenant como **capability** | **Não** — principalmente **FK** + exceções **[Código]** |
| Catálogos/políticas por cliente | **Parcial no modelo**; **risco** na execução **[Hipótese][Segregação]** |
| Branding/config por cliente | **Parcial** (`ParametroSite`, Loja/*) **[Código]**; copy Sebrae fixa em pontos **[Código]** |
| Evolução famílias de produto | **Fato:** `ETipoProduto` existe **[Código]** — **boa ancora** para modularização **[Recomendação]** |
| Capabilities transversais (segurança, observabilidade) | **Insuficientes** para plataforma **[Código]** (config API/auth) |

### 3.3 Lente de evolução (`14-platform-evolution-lens.md`)

- **Bom o suficiente para MVP legado:** jornada de compra para um contexto único ainda pode operar **[Hipótese][Teste manual]**.  
- **Necessário para operar bem como plataforma:** tenant context explícito, auth consistente, observabilidade, config auditável — **gap**.  
- **Necessário para escalar:** adapters, remoção de hardcodes, testes MT — **gap**.  
- **Desejável / pode esperar:** microsserviços, SAGA distribuída — alinhado a `02-modernization-goals` / `05-opinionated-architecture-baseline`.

---

## 4. Auditoria de capacidades

Cada linha: **ID** (Fase 2), **Classificação (12)**, **Justificativa**, **Valor futuro**, **MVP?**, **Manter / isolar / evoluir / remover**.

| ID | Capacidade | Classe | Justificativa | Valor futuro | MVP plataforma | Ação |
|----|------------|--------|---------------|--------------|----------------|------|
| — | **Tenant resolution & policy** | **CP** | Plataforma exige contexto tenant explícito (`11`) | Isolamento e config | **Sim** | **Evoluir** (não existe como capability hoje **[Hipótese]**) |
| — | **IAM + RBAC por tenant** | **CP** | Segurança transversal (`11`) | Escalar clientes | **Sim** | **Evoluir** |
| — | **Observabilidade / auditoria de negócio** | **CP** | Operação e compliance (`14`) | Suporte e incidentes | Mínimo **Sim** | **Evoluir** |
| C01 | Vitrine / descoberta | **CT** | Núcleo da loja | Receita | **Sim** | Manter comportamento; evoluir UX |
| C02 | Catálogo cursos/eventos | **CT** + **LEG** | Core + acoplamento disponibilidade Sebrae (RB-003) | Alto no segmento | Parcial MVP | **Isolar** disponibilidade em adapter |
| C03 | Catálogo conteúdos | **CT** | Downloads na vitrine | Médio | **Sim** (tipo Download) | Manter |
| C04 | Ficha produto | **CT** | Conversão | Alto | **Sim** | Manter |
| C05 | Sacola / pedido | **CT** | Transação | Alto | **Sim** | Manter; evoluir idempotência **[Hipótese]** |
| C06 | Cupom | **CT** | Promoção | Médio | Opcional MVP1 | Manter; **[Segurança]** em APIs |
| C07 | Checkout / venda / termos / inscrição paga | **CT** + **LEG** | Núcleo + financeiro Sebrae (RB-009) | Crítico | Core **Sim**; Sebrae **pós-MVP1** | **Isolar** pagamento Sebrae |
| C08 | Conta / identidade | **CP** + **COM** | Acesso | Obrigatório | **Sim** | Evoluir políticas |
| C09 | Cliente PF/PJ + integração | **CT** + **LEG** | Comprador + Sebrae | Alto no segmento | Sim com escopo | **Isolar** integração |
| C10 | Reserva / inscrição evento | **LEG** | Específico ecossistema | Alto se mandatório | **[Negócio]** | Manter atrás adapter |
| C11 | Pós-compra | **CT** | Confiança | Alto | **Sim** (mínimo) | Manter |
| C12 | Institucional / FAQ / Fale Conosco | **SUP** | Confiança marca | Médio | Parcial | Manter |
| C13 | Depoimentos | **SUP** | Marketing | Baixo/médio | Não MVP1 | Manter |
| C14 | Lead | **SUP** | Geração demanda | Médio | Não MVP1 | **Evoluir** MT (RB-012) |
| C15 | Consultoria integrada | **LEG** | Vertical | Se necessário | **[Negócio]** | **Isolar** |
| C16 | Sync evento Sebrae | **LEG** | Operação catálogo | Alto no segmento | Pós-MVP1 | **Isolar** |
| C17 | Config loja por empresa | **CP** + **SUP** | Branding tenant (`11`) | Alto | **Sim** | Evoluir audit trail |
| C18 | Admin cadastros auxiliares | **SUP** + **CT** | Operação | Alto | **Sim** (mínimo) | Manter |
| C19 | Dashboard / relatórios | **SUP** | Operação | Médio | Parcial | **[Segurança]** rota a rota |
| C20 | Upload | **COM** | Infra de conteúdo | Médio | Opcional | Manter |
| C21 | E-mail transacional | **SUP** | Pós-compra | Alto | **Sim** | **Evoluir** templates por tenant; remover copy fixa Sebrae **[DESC]** do comportamento padrão |
| C22 | Pagamento (projeto legado) | **CT** + **LEG** | Gateway | Alto | **[Teste manual][Negócio]** | Clarificar escopo antes de MVP2 |

**Obs.:** Linhas “—” são capabilities **exigidas pela plataforma-alvo** que **não** aparecem como módulo nomeado no legado — **gap de produto** **[Hipótese]**.

---

## 5. Auditoria de fluxos

Escala: **Forte | Razoável | Frágil | Incompleto**. Sempre que houver dúvida: **[Teste manual]** ou **[Segregação]**.

| Fluxo | Objetivo | Maturidade | Riscos / falhas | Controles ausentes | Validação |
|-------|----------|------------|-----------------|-------------------|-----------|
| **Onboarding / config tenant** | Provisionar novo cliente | **Incompleto** | Sem jornada produto documentada | Wizard, SLAs, auditoria | **[Negócio][Teste manual]** |
| **Gestão de loja** | Branding/conteúdo | **Razoável** | Quem altera o quê | Policy + audit | **[Segregação]** admin |
| **Catálogo** | Publicar/listar | **Razoável** / **Frágil** (cursos) | RB-003 | Adapter disponibilidade | **[Fluxo][Código]** + **[Teste manual]** |
| **Categorias / locale** | Navegação | **Razoável** | País/Estado sem `IdEmpresa` **[Código]** | Modelagem MT dados mestre | **[Negócio][Segregação]** |
| **Preço e desconto** | Monetização | **Razoável** | Manipulação via API **[Hipótese]** | AuthZ forte | **[Segurança][Teste manual]** |
| **Cliente** | Cadastro comprador | **Frágil (MT)** | RB-011 | Tenant explícito | **[Segregação][Código]** |
| **Carrinho / pedido** | Sacola | **Razoável** | Colisão código, semântica status | Idempotência checkout | **[Teste manual]** |
| **Checkout / venda** | Fechar compra | **Frágil** | RB-007 atomicidade **[Hipótese]** | Transação/SAGA se necessário | **[Teste manual][Negócio]** |
| **Pagamento** | Cobrar | **Frágil / Incompleto** plataforma | RB-009, múltiplos caminhos **[Hipótese]** | Config por tenant | **[Teste manual][Segurança]** |
| **Operação admin** | Backoffice | **Razoável** | Expor dados sensíveis **[Código] exemplo Dashboard** | AuthZ | **[Segurança]** |
| **Integração legado** | Sebrae | **Frágil escala** | Acoplamento, falhas externas | Circuit breaker, DLQ produto | **[Teste manual]** |
| **AuthN / AuthZ** | Acesso seguro | **Frágil** | CORS, insecure HTTP **[Código]** | Hardening, matriz rota×perfil | **[Segurança]** |

**Inconsistências front × back:** **Fato [Fluxo]:** documentação cruzada em `04-fluxos.md` + attribute routes; **recomendação:** inventário automatizado para fechar gaps residuais **[Teste manual]** não substituído por documentação.

**Regras duplicadas/ausentes:** lógica concentrada em **App Services** sem invariantes ricas nas entidades **[Código]** — risco de divergência entre API e jobs **[Hipótese]**.

---

## 6. Auditoria de segurança e segregação

| Tema | Achado | Tipo evidência | Ações de validação |
|------|--------|----------------|-------------------|
| **Tenant leakage (dados)** | `GetAll().First()` empresa em lead/cliente/consultoria | **[Código]** RB-010–012 | **[Segregação]** testes dois tenants + **[Negócio]** |
| **Tenant leakage (notificações)** | E-mail lead usa “primeira” empresa RB-012 | **[Código]** | **[Segregação][Teste manual]** |
| **Autorização** | Padrões permissivos API; exemplo histórico `[Authorize]` comentado Dashboard | **[Código]** discovery | **[Segurança]** pentest / review |
| **Sessão / token** | OAuth + Angular Bearer | **[Código][Fluxo]** | **[Segurança]** token replay, expiração, escopo |
| **Manipulação preço/cupom** | Preço no produto; cupom validado serviço — superfície API a mapear | **[Hipótese]** | **[Segurança][Teste manual]** |
| **Checkout / pedido / pagamento** | Estado inconsistente se falha intermediária RB-007 | **[Código]** lacuna | **[Teste manual][Negócio]** |
| **Integrações** | Credenciais, PII em logs, timeout | **[Hipótese]** | **[Segurança][Teste manual]** |

**O sistema atual é base funcional confiável?**  
**Fato:** há **valor** e **comportamento** reutilizável como **especificação comportamental parcial**. **Recomendação:** **não** usar como base **sem** corrigir MT e segurança; **não** assumir equivalência “rodou em produção” = “seguro para N tenants” **[Negócio][Segregação]**.

---

## 7. Famílias de produto / grupos modulares possíveis

| Família | Ancoragem legado | Capabilities compartilhadas (core) | Específicas | MVP plataforma | Evolução |
|---------|------------------|-------------------------------------|-------------|----------------|----------|
| **Eventos / educação** | `ETipoProduto` Curso, Evento; RB-003; sync F10 | Catalog, Order, Checkout, Customer | Availability adapter Sebrae, possível calendário/vagas **[Hipótese][Negócio]** | Tipos simples primeiro; **Sebrae pós-MVP1** | Módulo **Education/Events** |
| **Conteúdo digital** | Download; RB-002 pós-venda | Catalog, Order, Checkout | Entitlement/download | **Sim** | Módulo **Digital Fulfillment** |
| **Consultoria / serviço** | Consultoria; F9; RB-010 | Catalog, Customer, Integration | Fluxo deep-link / B2B leve **[Negócio]** | Se mandatório | Módulo **Services** |
| **Pacote / combo** | Pacote `ETipoProduto` | Pricing, Catalog | Composição, bundle pricing **[Hipótese][Negócio]** | Tardio | Módulo **Bundles** |
| **Retail físico (genérico)** | **Não** evidenciado como núcleo | — | Estoque, frete, SKU **[Hipótese]** | Fora escopo imediato | Só se estratégia mudar |

---

## 8. Oportunidades de melhoria

| Horizonte | Itens | Lente `14` |
|-----------|-------|------------|
| **Curto** | Matriz rota×auth×tenant; desligar insecure patterns em prod; eliminar fallback empresa | Operação + segurança |
| **Médio** | Adapters Sebrae; parametrizar financeiro; templates e-mail por tenant; observabilidade de jornada | Plataforma + operação |
| **Longo** | Pagamentos modernos (`02-modernization-goals`); extração seletiva (`05`); eventos/outbox onde compensação for real | Escalabilidade |

**Manter:** semântica Pedido/Venda, tipos de produto, cupom com janela — como **comportamento de referência** **[Código]**, refinado.  
**Reescrever:** camada de **tenant + segurança** como **primeira** prioridade de plataforma — não como “refactor opcional”.  
**Isolar:** todo **LEG** Sebrae listado na §4.  
**Descontinuar:** padrões **DESC** — copy/branding hardcoded, constantes financeiras em código **[Código]**.

---

## 9. Estrutura alvo da futura plataforma

Alinhado ao auditor + `07-platform-development-planner` + `11`:

1. **Tenant & Channel Management** (CP)  
2. **Identity & Access** (CP)  
3. **Storefront Experience** (CT)  
4. **Catalog & Product** (CT)  
5. **Availability & Entitlement** (CT/SUP) — *novo como capability explícita*  
6. **Pricing & Promotions** (CT)  
7. **Customer & Consents** (CT)  
8. **Order & Checkout** (CT)  
9. **Payment & Settlement** (CT)  
10. **Operations, Reporting & Analytics** (SUP)  
11. **Integration Adapters** (LEG contido) — Sebrae, gateway, CEP…  
12. **Platform** — segurança, observabilidade, feature flags, jobs (CP)

**Transversais obrigatórias (`11`):** segregação, auditoria de config, política comercial por tenant.

---

## 10. Plano de desenvolvimento por fases (orientação)

| Fase | Objetivo | Capabilities entregues (pacote) | Dependências | Riscos | Critério de sucesso |
|------|----------|----------------------------------|--------------|--------|---------------------|
| **0 — Fundação** | Não construir em cima de risco cego | Inventário rotas; matriz segurança; constraints negócio preenchidas; smoke dois tenants | Acesso ambiente | Escopo | Lista crítica classificada + 1º teste MT documentado |
| **1 — MVP plataforma** | Compra segura multi-tenant mínima | Tenant context; IAM; Storefront mín; Catalog neutro; Order; Sale; Customer scoped; notificação mínima; observabilidade mínima | Decisão tipos produto MVP | Paridade Sebrae | 2 tenants isolados UAT |
| **2 — Paridade + adapters** | Valor segmento | Availability Sebrae; financeiro parametrizado; cadastro integrado; sync; consultoria | Contratos externos | Indisponibilidade | Checklist UC paridade |
| **3 — Modernização comercial** | PIX/link/checkout transparente; multi-marca | Payment evolution; templates | Adquirentes | Compliance | KPI conversão + redução incidentes |
| **4 — Escala** | Extração seletiva | Mensageria/outbox se necessário; serviços só com pressão (`05`) | Operação madura | Complexidade | SLO atendidos |

---

## 11. MVP da plataforma

**Definição (alinhada `02-product/09-mvp.md`):** demonstrar **dois tenants** com jornada de compra **sem** depender de integrações Sebrae obrigatórias para o **tipo de produto** escolhido no MVP.

**Inclui (capabilities):** Tenant resolution; IAM com política explícita; Storefront mínimo; Catalog + Product + Price; Order; Sale; Customer com `IdEmpresa` correto; Payment **registrado ou stub** acordado; observabilidade mínima de falha de checkout.

**Exclui (típico):** RB-009 obrigatório; RB-003 como única fonte de verdade; lead, lista desejos, relatórios completos — salvo **[Negócio]** mandar o contrário.

---

## 12. Pós-MVP

- Onda 2 e 3 da §10.  
- Famílias de produto adicionais (§7).  
- Hardening contínuo **[Segurança]**.  
- Readiness: mais tenants só com suite MT + segurança; mais famílias só com adapter + testes.

---

## 13. Alertas críticos

1. **FK `IdEmpresa` ≠ plataforma multi-tenant segura** **[Código]**.  
2. **CORS `*` + `AllowInsecureHttp`** não são aceitáveis como estado final de plataforma **[Código]**.  
3. **Hardcodes Sebrae** bloqueiam multi-cliente heterogêneo **[Código]**.  
4. **Dashboard/relatórios** sem hardening = vazamento operacional **[Código][Segurança]**.  
5. **Constraints** do projeto em branco (`03-constraints.md`) = risco de programa **[Negócio]**.

---

## 14. Itens que exigem validação adicional

| Item | Tipo |
|------|------|
| Atomicidade pedido→venda→pagamento | **[Teste manual][Negócio]** |
| Escopo completo `MaisCode.Pagamento` | **[Teste manual][Negócio]** |
| Função e criticidade `winService` | **[Teste manual][Negócio]** |
| Todas as rotas admin e dados expostos | **[Segurança]** |
| Isolamento real com 2+ empresas ativas | **[Segregação]** |
| Vínculo cupom × linhas do pedido | **[Código]** incompleto na discovery → **[Teste manual]** |
| UC-13 inscrição/cancelamento servidor | **[Teste manual]** |
| “Uma ou N lojas por tenant” | **[Negócio]** |
| Unicidade/colunas `Integracao.EventoCurso` entre tenants | **[Segregação][Negócio]** + DBA |

---

## 15. Próximos passos recomendados

1. Preencher **`03-constraints.md`** com patrocinador de negócio.  
2. Executar **Fase 0** (§10): artefatos de segurança + MT smoke.  
3. Congelar **MVP plataforma** vs **MVP paridade Sebrae** com critérios mensuráveis.  
4. Incorporar **`13-validation-and-testing-needs`** na definição de DoD de cada capability.  
5. Revisar periodicamente com **`14-platform-evolution-lens`** (configurável? observável? modular?).

---

## Plano de desenvolvimento do sistema — orientado por capabilities

*(Não é lista de tarefas técnicas soltas; cada bloco é um **pacote de capability** com resultado verificável.)*

| Onda | Pacote de capability | Resultado verificável |
|------|----------------------|------------------------|
| **0** | **Governança + evidência** | Matriz rota×método×auth×tenant; relatório de riscos; constraints assinadas; 1º roteiro teste MT |
| **0** | **Hardening mínimo legado** (se manter no ar) | CORS/HTTPS/Authorize revisados em endpoints críticos **[Segurança]** |
| **1** | **Tenant & IAM** | Contexto tenant injetado em fluxos críticos; sem `GetAll().First()` nos paths MVP **[Segregação]** |
| **1** | **Storefront + Catalog (neutro)** | Vitrine + detalhe + listagem para tipo produto MVP |
| **1** | **Order → Sale** | Jornada fechada com registro de pagamento acordado + trilha de incidente |
| **1** | **Observabilidade mínima** | Falha de checkout/checkout integration visível para operação |
| **2** | **Adapter Sebrae — Availability** | Curso/evento com fonte plugável; RB-003 atrás de porta |
| **2** | **Adapter Sebrae — Financeiro** | RB-009 vira configuração por tenant/ambiente |
| **2** | **Adapter cadastral** | CPF/CNPJ atrás de porta; política erro/timeout |
| **3** | **Payment moderno + brand** | Meios adicionais; templates multi-marca |
| **4** | **Scale-out seletivo** | Apenas onde `05` e métricas justificarem |

**Dependência lógica:** Onda **0** → **1** → **2**; **3** paralelizável parcialmente após **1**; **4** só após operação estável.

---

## Referências

- Discovery: `.ai/outputs/01-discovery/`  
- Produto: `.ai/outputs/02-product/`  
- Auditoria anterior (resumo): `.ai/outputs/03-strategic-platform-audit/00-analise-estrategica-plataforma.md`
