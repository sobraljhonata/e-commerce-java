# Riscos, dependências e dúvidas — Fase 2

**Fontes:** Fase 1 (`07-lacunas-e-riscos.md`, `05-acoplamentos-sebrae.md`, `06-analise-multi-tenant.md`), agentes Fase 2, `.ai/context/02-modernization-goals.md`.

**Nota (alinhamento 2026-04-07):** a Fase 1 em `.ai/outputs/01-discovery/` foi **atualizada** após `git checkout` dos projetos corrompidos/ausentes. Controllers, `WebApiConfig`, `BootStrapper`, `Startup.Auth` e `SebraeIntegracao/Metodos` estão **legíveis** no workspace. **Ainda** é necessário executar a **auditoria rota × auth × tenant** e o build — isso é trabalho operacional, não bloqueio de leitura de código.

---

## 1. Riscos de produto e negócio

| ID | Risco | Probabilidade / Impacto | Mitigação (produto) |
|----|-------|-------------------------|---------------------|
| R-01 | **Vazamento ou gravação no tenant errado** por uso de “primeira empresa” (RB-010–012) | Alta / Alto | Épico E-TEN + histórias MT; testes de aceitação multi-empresa antes de go-live |
| R-02 | **Catálogo indisponível** quando integração Sebrae ou `EventoCurso` falha (RB-003) | Média / Alto | Feature de degradação controlada + mensagens claras; roadmap V3 |
| R-03 | **Quebra financeira** por mudança de IDs de forma de pagamento (RB-009) | Média / Alto | US-071 parametrização; gestão de configuração por ambiente |
| R-04 | **Branding Sebrae fixo** em e-mails e URLs (RB-008, acoplamentos) | Alta / Médio | F-CMS-04 templates por empresa |
| R-05 | **Inconsistência pedido×venda** se falha no meio do fluxo (RB-007 lacuna atomicidade) | Média / Alto | T-D04 + política de reconciliação manual/automática definida pelo negócio |
| R-06 | **Autorização fraca** em APIs administrativas (exemplo `Dashboard` com `[Authorize]` comentado na Fase 1) | ? / Alto | Reverificação pós-restore + política IAM explícita |
| R-07 | **Colisão ou ambiguidade** em dados `Integracao` compartilhados sem `IdEmpresa` em `EventoCurso` (hipótese Fase 1) | Média / Médio | Workshop com DBA + decisão de ownership por tenant |
| R-08 | **Serviço Windows** (`MaisCode.WinService`) — função e agendamento em produção | ? / Médio | UC-D1 — confirmar com time (código **presente** após restore) |

---

## 2. Riscos de programa (entrega)

| ID | Risco | Mitigação |
|----|-------|-----------|
| RP-01 | Escopo “paridade total” inclui **toda** integração Sebrae — explode timeline | Cortar MVP 1 sem integrações não críticas; usar `09-mvp.md` |
| RP-02 | Falta de testes automatizados legados | Definir conjunto mínimo de testes de aceitação manuais + dados sintéticos |
| RP-03 | Documentação de contexto (.NET Core 2.1) diverge do legado (.NET Framework na Fase 1) | Tratar contexto como **alvo**; paridade baseada em código |

---

## 3. Dependências

| ID | Dependência | Bloqueia | Tipo |
|----|-------------|----------|------|
| D-01 | **Definição de tenant** (host, claims, convite) | IAM, carrinho, lead, consultoria | Negócio |
| D-02 | **Contratos Sebrae** (cadastro, financeiro, eventos) mantidos pelo Sebrae | Checkout, catálogo de cursos | Externa |
| D-03 | **Políticas jurídicas** (termos, LGPD, recibo) | UC-08, e-mails | Jurídico |
| D-04 | **Dados mestres** (locale, categorias) por empresa | Catálogo, filtros | Operação |
| D-05 | **Configuração de e-mail** (SMTP ou provedor) | Notificações | Infra (sem escolher produto agora) |
| D-06 | **Clarificação do projeto Pagamento legado** | Paridade de gateway | Discovery T-D06 |

---

## 4. Dúvidas em aberto (não inventar resposta)

| ID | Dúvida | Como esclarecer |
|----|--------|-----------------|
| Q-01 | Quantas empresas operam hoje em produção e com quais isolamentos reais? | Entrevista operação + auditoria BD |
| Q-02 | Login social: quais provedores e mapeamento para cliente? | Rever front + auth servidor pós-restore |
| Q-03 | Escopo exato de `ReportController` e consumidores | Inventário telas admin |
| Q-04 | `winService`: o que sincroniza e com que frequência? | Código + operação |
| Q-05 | Lista de desejos exige autenticação sempre? | UX atual + regras servidor |
| Q-06 | Pacotes (`ETipoProduto.Pacote`): composição e preço na UX? | `ProdutoService` + negócio |
| Q-07 | Integração CEP Sebrae é obrigatória para todas as empresas? | Negócio |
| Q-08 | `MaisCode.Pagamento` cobre quais bandeiras/métodos hoje? | Código + operação |

---

## 5. Hipóteses registradas (produto)

| ID | Hipótese |
|----|----------|
| H-01 | Existe apenas **uma** loja pública por empresa por ambiente (URL dedicada). |
| H-02 | Perfil administrativo sempre ligado a uma empresa via `Perfil.IdEmpresa` (Fase 1). |
| H-03 | Curso “disponível” sem Sebrae **não** é cenário de produção atual — seria evolução. |

---

## 6. Ligação com objetivos de modernização (contexto)

De `.ai/context/02-modernization-goals.md`, estes itens viram **riscos se omitidos** no roadmap:

- Pagamentos modernos (PIX, link, checkout transparente) — **não** são paridade do legado descrito na Fase 1; entram como **V2-MOD** / pós-MVP.
- Observabilidade e segurança — **E-OBS** e **F-IAM-04/05**.

---

## 7. Checklist pós-restauração Git

1. ~~Atualizar documentos da Fase 1~~ **feito** (discovery alinhada ao workspace).
2. Regenerar lista de rotas públicas e admin; cruzar com `04-fluxos.md`.
3. Confirmar `[Authorize]` e políticas por rota vs modelo MT desejado.
4. Build + testes integrados (`IntegraTestes`, cenários críticos Pagamento).
