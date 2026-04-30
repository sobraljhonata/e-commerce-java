# Análise estratégica: legado → plataforma de e-commerce

**Agentes:** `.ai/agents/phase-2-product/04-ecommerce-strategy-auditor.md`, `.ai/agents/phase-3-modernization/07-platform-development-planner.md`  
**Contextos:** `.ai/context/*` (ênfase em `11-ecommerce-platform-principles.md`, `05-opinionated-architecture-baseline.md`, `02-modernization-goals.md`)  
**Entradas:** `.ai/outputs/01-discovery/`, `.ai/outputs/02-product/`  
**Data:** 2026-04-07  

**Legenda:** **Fato** = evidência em discovery/product/código documentado · **Hipótese** = não fechado sem validação em runtime · **Recomendação** = direção desejada para plataforma (sem fixar tecnologia aqui).

---

## 1. Diagnóstico executivo

### 1.1 Maturidade do e-commerce transacional

| Dimensão | Avaliação | Base |
|----------|-----------|------|
| **Descoberta e vitrine** | **Razoável a forte** para o caso de uso original | Fluxos F1–F3; múltiplos tipos de produto (`ETipoProduto`) |
| **Sacola / pedido / venda** | **Razoável** com **fragilidades** | RB-004–007; separação Pedido vs Venda **Fato**; atomicidade pedido→venda **não confirmada** **Hipótese** |
| **Checkout e pagamento** | **Frágil para plataforma** | Integração financeira Sebrae com constantes (RB-009) **Fato**; `MaisCode.Pagamento` existe **Fato**, papel fim-a-fim **Hipótese** até testes |
| **Pós-compra** | **Razoável** | Histórico, downloads, lista de desejos **Fato** (Fase 2 UC) |

**Síntese:** o sistema é um **e-commerce verticalizado** (forte presença Sebrae em catálogo, cadastro e financeiro), **não** um retail genérico neutro. Faz sentido **como loja** para um ecossistema; **não** faz sentido **como plataforma SaaS** sem redesign explícito de tenant, integrações e políticas comerciais.

### 1.2 Maturidade multi-tenant / “plataforma”

| Critério (`11-ecommerce-platform-principles.md`) | Situação legado |
|--------------------------------------------------|-----------------|
| Tenant como **capacidade de plataforma** | **Não atendido** — `IdEmpresa` como FK **Fato**; fallback “primeira empresa” em vários fluxos **Fato** (RB-010–012) |
| Configuração explícita e auditável por cliente | **Parcial** — `ParametroSite` etc. por empresa **Fato**; hardcodes e copy Sebrae **Fato** |
| Catálogo e políticas variando por tenant | **Parcial no modelo**; **risco no comportamento** | Filtros e queries a revisar **Recomendação** discovery |
| Integrações isoladas | **Fraco** — campos Sebrae no `Produto`, SQL a `Integracao.EventoCurso` **Fato** |

**Síntese:** **Fato:** o legado é **multi-empresa no schema**, com sinais claros de **single-tenant no comportamento** em pontos críticos. **Hipótese:** em produção com uma única empresa ativa, o problema pode estar “oculto”. **Recomendação:** não declarar conformidade multi-tenant até auditoria rota-a-rota + testes com dois tenants.

### 1.3 Principais riscos (prioridade)

1. **Segregação** tenant/cliente (dados e notificações no tenant errado). **Fato** (padrão `GetAll().First()`).  
2. **Segurança de API** (CORS `*`, `AllowInsecureHttp`, `[Authorize]` comentado em exemplos). **Fato** (discovery).  
3. **Acoplamento Sebrae** bloqueando evolução multi-cliente. **Fato**.  
4. **Fragilidade checkout/pagamento** (constantes, dependência de integração). **Fato**.  
5. **Stack e front legados** — custo de segurança e time. **Fato**.

### 1.4 Principais oportunidades

- **Modular monólito** com adapters de integração (alinhado a `05-opinionated-architecture-baseline.md`).  
- **Famílias de produto** já sugeridas pelo domínio (`ETipoProduto`) — agrupar módulos por jornada.  
- **North Star (planejador):** *“Operar várias organizações (tenants) com catálogos e políticas próprias, checkout confiável e integrações opcionais por tenant, com segregação e observabilidade de primeira classe.”* **Recomendação**

---

## 2. Auditoria de capacidades

Classificação pedida pelo auditor (por macro-capacidade consolidada da Fase 2):

| Capacidade (resumo) | Classificação | Notas |
|----------------------|---------------|--------|
| **Tenant / Empresa** | Core de plataforma (alvo) · **Legado frágil** (hoje) | `IdEmpresa` sem política uniforme **Fato** |
| **IAM (login, perfil, admin)** | Core transacional + Core de plataforma | OAuth/cookies **Fato**; matriz permissão×tenant **Hipótese** |
| **Storefront / vitrine** | Core transacional | AngularJS **Fato** |
| **Catálogo + tipos de produto** | Core transacional + **Supporting** | Tipos múltiplos **Fato**; disponibilidade curso **Legado Sebrae** |
| **Preço** | Core transacional | No produto **Fato**; políticas por tenant **Hipótese** |
| **Promoções / cupom / campanha** | Core transacional | RB-006 **Fato** |
| **Cliente PF/PJ** | Core transacional | Integração cadastral **Legado Sebrae** |
| **Pedido (sacola)** | Core transacional | **Fato** |
| **Venda / checkout** | Core transacional | **Fato**; integração financeira **Legado Sebrae** |
| **Pagamento (adquirente)** | Core transacional | Projeto Pagamento **Fato**; encadeamento completo **Hipótese** |
| **Conteúdo / CMS loja** | Supporting | FAQ, HTML, banners **Fato** |
| **Lead / social** | Supporting · **Commodity** | Lead com risco MT **Fato** |
| **Relatórios / dashboard** | Operations / analytics | Rota dashboard **Fato** |
| **Integração Sebrae (Metodos)** | **Legado / acoplamento específico** | HTTP clients **Fato** |
| **WinService** | Supporting / integration · **Candidata a módulo batch** | Presença **Fato**; função **Hipótese** |
| **CupomSvc utilitário** | Commodity / supporting | **Fato** |

**Candidatas a descontinuação (com análise de negócio):** copy e URLs hardcoded “Sebrae”; constantes financeiras em código **Recomendação** substituir por configuração por tenant, não necessariamente “desligar” integração.

---

## 3. Auditoria de fluxos

| Fluxo | Objetivo | Maturidade | Riscos / falhas | Controles ausentes | Recomendação plataforma |
|-------|----------|------------|-----------------|--------------------|-------------------------|
| **Onboarding / configuração de tenant** | Provisionar empresa, branding, parâmetros | **Incompleto** como produto | Sem jornada documentada ponta a ponta **Hipótese** | Wizard, checklist, auditoria de config **Recomendação** | Capability **Tenant Lifecycle** explícita |
| **Gestão de loja** | Banners, menus, temas, parâmetros | **Razoável** | Por empresa no modelo **Fato** | Validação de quem altera o quê **Hipótese** | Policy + audit log |
| **Catálogo** | Publicar e listar produtos | **Razoável** a **frágil** (cursos) | RB-003 acoplamento **Fato** | Fonte de verdade de disponibilidade isolada **Recomendação** | **Catalog + Availability** adapter |
| **Categorias / temas / locale** | Navegação e filtros | **Razoável** | `Pais`/`Estado` sem `IdEmpresa` **Fato** | Impacto MT a validar **Hipótese** | Revisar dados mestres globais vs por tenant |
| **Preço e desconto** | Precificar e promover | **Razoável** | Manipulação de preço via API **não auditada** **Hipótese** | Autorização forte em alteração de preço/cupom **Recomendação** | **Pricing & Promotions** com políticas |
| **Cliente** | Cadastro comprador | **Frágil (MT)** | RB-011 **Fato** | Contexto tenant explícito **Recomendação** | **Customer** com scoping obrigatório |
| **Carrinho / pedido** | Sacola persistente | **Razoável** | Colisão código pedido probabilística **Fato** | Idempotência em checkout **Recomendação** | **Order** com correlação idempotency-key |
| **Checkout / venda** | Concluir compra | **Frágil** | RB-007 atomicidade **Hipótese** | Saga/outbox quando distribuído **Recomendação** (só se necessário — `05`) | Transação clara pedido→venda→pagamento |
| **Pagamento** | Cobrar e registrar | **Frágil / incompleto** para plataforma | RB-009 **Fato** | Config por tenant **Recomendação** | **Payment** + **Integration adapter** financeiro |
| **Operação admin** | Backoffice | **Razoável** | Dashboard sem authorize histórico **Fato** | RBAC + tenant **Recomendação** | **Admin** module |
| **Integração legado** | Sebrae diversos | **Frágil para escala** | Múltiplos pontos de acoplamento **Fato** | Circuit breaker, DLQ como produto **Recomendação** | **Integration** bounded context |
| **Autenticação / autorização** | Acesso seguro | **Frágil** | CORS *, insecure HTTP flag **Fato** | Matriz rota×role×tenant **Recomendação** | **Platform security** transversal |

**Crítica de “faz sentido para plataforma?”**  
- **Fato:** os fluxos fazem sentido para **uma** organização ou um conjunto homogêneo (Sebrae).  
- **Recomendação:** para **multi-cliente heterogêneo**, é necessário **desacoplar** disponibilidade de curso, financeiro e cadastro, e **parametrizar** o que é hoje implícito.

**Validação manual sugerida:** percorrer checkout completo em staging com **dois** `IdEmpresa`; tentar acessar recurso do tenant B com token do tenant A; repetir para dashboard e relatórios.

---

## 4. Auditoria de segurança e segregação

### 4.1 Isolamento entre tenants

| Tema | Fato | Risco | Recomendação |
|------|------|-------|--------------|
| Fallback “primeira empresa” | **Fato** RB-010–012 | Alto — dados e e-mails no tenant errado | Eliminar; contexto tenant obrigatório |
| `EventoCurso` sem `IdEmpresa` no domínio | **Fato** discovery | Médio — colisão / vazamento lógico **Hipótese** | Ownership por tenant ou staging |
| Queries sem filtro empresa | **Fato** parcial (`ProdutoService` citado) | Médio | Code review sistemático |
| Dados de integração compartilhados | **Fato** / **Hipótese** | Médio | Política explícita |

### 4.2 Cliente (comprador) vs tenant (organização)

- **Fato:** cliente ligado a `IdEmpresa`; usuário com `Perfil` e possível ligação indireta à empresa.  
- **Hipótese:** mesma pessoa física em dois tenants pode exigir contas distintas — comportamento atual não documentado.  
- **Recomendação:** definir produto: *identidade global* vs *conta por loja*.

### 4.3 Autenticação / autorização / superfície de API

| Item | Fato | Alerta |
|------|------|--------|
| CORS `*` | **Fato** `WebApiConfig` | Alto em produção sem camada restritiva |
| `AllowInsecureHttp = true` (OAuth server) | **Fato** `Startup.Auth` | Crítico se vazado a produção |
| `[Authorize]` comentado (ex. Dashboard) | **Fato** | Exposição de dados operacionais |
| Bearer + interceptor Angular | **Fato** | Depende de validação server-side consistente **Hipótese** |

### 4.4 Manipulação de preço, cupom, pedido

- **Hipótese:** endpoints que aceitam `IdEmpresa` ou valores de preço do cliente sem validação server-side são vetores de fraude — **validar manualmente** nos controllers.  
- **Recomendação:** preço efetivo calculado **servidor**; cupom com limite por tenant e auditoria.

### 4.5 Integrações

- **Fato:** chamadas HTTP a URLs relativas (`urlService`); credenciais e erros **Hipótese** até revisar config.  
- **Recomendação:** secrets por tenant/ambiente, logs sem PII indevido, timeout e idempotência em POST financeiros.

### 4.6 Alertas prioritários

1. Corrigir **autorização** e **HTTPS** antes de escalar tenants.  
2. Eliminar **fallback de empresa**.  
3. Auditar **lista de rotas** × **auth**.  
4. Tratar **Sebrae** como **adapter** com contrato e limites.

---

## 5. Oportunidades de melhoria

*(Síntese dos horizontes do auditor + princípios da plataforma)*

### Curto prazo (antes / durante migração)

- Matriz **rota × método HTTP × auth × tenant scope** **Recomendação**  
- Testes manuais dois-tenants **Recomendação**  
- Desligar padrões inseguros em ambientes não-dev **Recomendação**  
- Documentar `MaisCode.Pagamento` e fluxo `winService` **Fato** gap

### Médio prazo

- **Anti-corrupção** para Sebrae (catálogo, financeiro, cadastro) **Recomendação**  
- **Parametrização** de IDs e templates de e-mail **Recomendação**  
- **Observabilidade** de jornada de compra e integrações **Recomendação** (`02-modernization-goals`)

### Longo prazo

- **Pagamentos modernos** (PIX, link, checkout transparente) **Recomendação** contexto  
- Evolução **modular monolith → extração seletiva** **Recomendação** `05`  
- **Eventos / outbox** em pedido-pagamento-notificação quando compensação for real **Recomendação** `05`

### O que manter como comportamento de produto

- Separação **Pedido** / **Venda**, tipos de produto, cupom com janela, código de pedido amigável **Fato** — refinar, não descartar às cegas.

### O que descontinuar como padrão

- “Primeira empresa”; copy fixa Sebrae; constantes financeiras em código **Recomendação**

---

## 6. Estrutura alvo da plataforma

### 6.1 Princípios (produto + arquitetura)

Derivados de `11-ecommerce-platform-principles.md` e `07-platform-development-planner.md`:

1. Tenant e segurança são **capabilities**, não só colunas.  
2. Integrações por **adapters** com contrato versionado.  
3. Evolução por **capability**, não só por camada.  
4. MVP: **compra confiável + MT mínimo** antes de “feature richness”.  
5. **Não** assumir microsserviços no primeiro salto (`02-modernization-goals`).

### 6.2 Macrodomínios e responsabilidades

| Macrodomínio | Responsabilidade |
|--------------|------------------|
| **Tenant & Channel** | Empresa, lojas/canais futuros, limites de plano, config auditável |
| **Identity & Access** | Usuários, perfis, RBAC, vínculo tenant, SSO futuro |
| **Storefront Experience** | Vitrine, busca, conteúdo, tema — consome catálogo neutro |
| **Catalog & Availability** | Produto, categorias, publicação, **disponibilidade plugável** |
| **Pricing & Promotions** | Preço, cupom, campanha, políticas por tenant |
| **Customer** | PF/PJ, endereços, consentimento |
| **Order & Checkout** | Sacola, pedido, termos, transição para venda |
| **Payment** | Orquestração de cobrança, estados, conciliação |
| **Operations & Reporting** | Admin, dashboard, relatórios |
| **Integration Platform** | Adapters Sebrae, pagamento, CEP, etc. |
| **Platform** | Observabilidade, auditoria, feature flags, jobs |

### 6.3 Famílias de produto → grupos de módulos

| Família | Tipos / jornada (legado) | Módulos sugeridos |
|---------|--------------------------|-------------------|
| **Educação / evento** | Curso, Evento, Pacote | Catalog + Availability (Sebrae adapter) + Order + possível **Scheduling/Seat** futuro **Hipótese** |
| **Conteúdo digital** | Download | Catalog + **Entitlement / Download** |
| **Serviço consultivo** | Consultoria | Catalog + **Integration consultoria** + Customer |
| **Transversal** | Todos | Pricing, Payment, IAM, Tenant |

**Hipótese:** Pacote pode exigir **composição** e preço bundle — validar regra de negócio com stakeholders.

---

## 7. Plano de desenvolvimento por fases

*(Capability-oriented, inspirado em `07` + `05` Fase 1–3)*

### Onda 0 — Fundação (4–8 semanas típico, variável)

**Objetivos:** segurança mínima, visibilidade, decisões de tenant.  
**Capabilities:** inventário rotas; matriz auth; testes dois tenants; constraints em `03-constraints.md` preenchidas com negócio.  
**Riscos:** escopo creep.  
**Critério de sucesso:** lista crítica de endpoints classificados + falhas MT reproduzíveis ou ausentes em smoke test.

### Onda 1 — Plataforma núcleo (MVP plataforma — alinhado à Fase 2 `09-mvp`)

**Objetivos:** **multi-tenant real** no fluxo principal de compra sem depender de Sebrae para “tipo simples”.  
**Capabilities:** Tenant context; IAM; Storefront mínimo; Catalog neutro; Order; Venda; Payment registrado ou stub; observabilidade mínima.  
**Dependências:** decisão produto sobre tipos de produto no MVP.  
**Critério de sucesso:** dois tenants isolados em UAT em jornada compra.

### Onda 2 — Paridade + adapters Sebrae

**Objetivos:** reintroduzir valor específico sem colar no core.  
**Capabilities:** Availability Sebrae; financeiro parametrizado; cadastro CPF/CNPJ; sync evento; consultoria corrigida MT.  
**Riscos:** dependência externa.  
**Critério de sucesso:** paridade acordada com checklist UC da Fase 2.

### Onda 3 — Modernização comercial e pagamentos

**Objetivos:** PIX / link / checkout transparente **Recomendação** contexto; templates multi-marca.  
**Dependências:** adquirentes e compliance.  

### Onda 4 — Escala e extração seletiva

**Objetivos:** mensageria/outbox onde processo distribuído for real; extrair serviço só com pressão comprovada (`05` Fase 3).  

### Sequência de modularização (alto nível)

1. **Integration Sebrae** atrás de portas estáveis.  
2. **Availability** separada de **Catalog** leitura vitrine.  
3. **Payment** desacoplado de **Venda**.  
4. **Reporting** como consumidor de eventos/read models **Hipótese** futura.

---

## 8. MVP da plataforma

**Definição:** mesmo recorte que `.ai/outputs/02-product/09-mvp.md` seção 2, interpretado como **MVP de plataforma** (não só paridade legado):

- **In:** contexto tenant explícito; IAM com política definida; storefront + catálogo + pedido + venda + cliente scoped; observabilidade mínima de falha de jornada.  
- **Out (até próxima onda):** RB-009 obrigatório; RB-003 obrigatório; várias integrações sociais/leads se não forem críticas ao piloto.

**North Star do MVP:** *“Dois tenants reais conseguem vender um produto simples com dados isolados e trilha de suporte a incidentes.”*

---

## 9. Pós-MVP

- Paridade Sebrae (Onda 2).  
- Pagamentos modernos e multi-marca (Onda 3).  
- Relatórios avançados, lead, lista desejos, login social conforme prioridade de negócio (Fase 2 MVP 2).  
- Evolução arquitetural sob critérios `05` Fase 2–3.

**Requisitos não funcionais prioritários** (planejador): segurança (auth, dados, integrações), observabilidade, testabilidade de fluxos críticos, custo operacional (evitar distribuição prematura), **readiness** para mais categorias de produto via adapters.

**Readiness para escalar clientes e categorias de produto**

| Gate | Critério |
|------|----------|
| **Mais tenants** | Testes automatizados ou manuais regressivos MT; sem fallback empresa |
| **Mais famílias de produto** | Adapter + políticas + testes de disponibilidade/checkout |
| **Mais integrações** | Contrato, limites de taxa, observabilidade, feature flag por tenant |

---

## 10. Alertas críticos e próximos passos

### Alertas críticos

1. **Não** tratar `IdEmpresa` como multi-tenant “resolvido”. **Fato comportamental.**  
2. **Não** expor dashboard/relatórios sem revisão de `[Authorize]` e escopo. **Fato.**  
3. **Não** replicar constantes financeiras em nova plataforma. **Fato.**  
4. **Constraints** em `03-constraints.md` estão em branco — **risco de programa** até preenchimento.  

### Próximos passos (ordem sugerida)

1. Preencher **constraints** com negócio (janela de migração, integrações obrigatórias, manutenção do legado).  
2. **Build** + inventário de rotas + matriz segurança.  
3. **Testes dois tenants** documentados (evidência).  
4. Congelar **MVP plataforma** vs **MVP paridade Sebrae** com pat patrocinador.  
5. Iniciar **Onda 0** com entregáveis de segurança e tenant context.

---

## Referências internas

- Discovery: `01-discovery/00`–`07`  
- Produto: `02-product/00`–`09`  
- Princípios plataforma: `context/11-ecommerce-platform-principles.md`  
- Baseline arquitetura: `context/05-opinionated-architecture-baseline.md`
