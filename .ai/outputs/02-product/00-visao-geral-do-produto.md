# Visão geral do produto — Fase 2 (Reconstrução)

**Fontes:** `.ai/agents/phase-2-product/01-product-reconstructor.md`, `.ai/context/*`, `.ai/outputs/01-discovery/*`.  
**Data:** 2026-04-07.  
**Princípio:** nenhuma capacidade listada aqui sem base na Fase 1; lacunas ficam como hipóteses ou itens de discovery.

---

## 1. O que é o produto (legado observado)

Plataforma de **e-commerce B2C** (e fluxos afins) voltada ao ecossistema **Sebrae**, com:

- **Loja pública (storefront)** consumindo API REST sob `/api/...` (contratos inferidos principalmente dos serviços AngularJS na Fase 1).
- **Back-office / admin** implícito pelos controllers da API (`Empresa`, `MenuAdm`, `Report`, `Dashboard`, `Upload`, etc.) e redirecionamento da raiz MVC para `/admin`.
- **Catálogo centrado em “Produto”** com tipos de negócio distintos: **Curso, Evento, Download, Consultoria, Pacote** (`ETipoProduto` — Fase 1).
- **Sacola modelada como Pedido** e **conclusão comercial como Venda**, com regras de cupom, termos de aceite e integração financeira Sebrae em fluxo de inscrição paga (Fase 1).
- **Modelo de dados multi-empresa** (`IdEmpresa` em quase todo o núcleo), porém com **comportamentos que assumem uma empresa padrão** em vários fluxos (débito legado — Fase 1).

---

## 2. Proposta de valor (como o sistema se sustenta hoje)

| Para quem | Valor entregue (evidência Fase 1) |
|-----------|-------------------------------------|
| **Visitante** | Navegar vitrine (home, cursos/eventos, conteúdos), ver detalhes de produto, proximidade geográfica opcional, depoimentos. |
| **Cliente** | Sacola, cupom, checkout, conta, histórico de compras, downloads pós-venda, lista de desejos, cadastro PF/PJ. |
| **Operação / admin** | Gestão de empresa e parâmetros de loja, catálogo e conteúdo (APIs nomeadas na discovery), relatórios/dashboard (pelo menos `GET api/dashboard/obter-dados` confirmado em C# na Fase 1). |
| **Ecossistema Sebrae** | Sincronização de eventos/cursos, cadastro e reservas/inscrições via integrações, registro financeiro na inscrição paga, disponibilidade de curso amarrada a `Integracao.EventoCurso` (Fase 1). |

---

## 3. Fronteira: núcleo de e-commerce × customização Sebrae

**Núcleo genérico de e-commerce (reutilizável conceitualmente):** vitrine, catálogo com categorias/temas, detalhe de item, sacola/pedido, desconto por cupom, venda, cliente, conteúdo institucional (HTML/FAQ/Fale Conosco), captura de lead, lista de desejos, autenticação.

**Específico ou fortemente influenciado pelo Sebrae (Fase 1):**

- Campos e regras de **produto** ligados a evento/situação/migração Sebrae.
- **Disponibilidade de curso** dependente de `Integracao.EventoCurso` e regras locais (RB-003).
- **Cadastro e consultas** PF/PJ com integrações (`consultaPessoa*Integracao`, fluxos em `UserSvc.js`).
- **Financeiro:** `EfetuarInscricaoPaga` com payload para integração e **IDs fixos** de forma de pagamento (RB-009).
- **E-mails e URLs** com branding/copy Sebrae (RB-008, tabela de acoplamentos).
- **Sincronização** produto/evento (`Sync/Eventos/{CodEvento}`).

---

## 4. Classificação transversal (obrigatória no backlog)

Cada item do backlog (Fase 2) deve ser classificável em:

| Rótulo | Significado |
|--------|-------------|
| **Capacidade atual** | Comportamento já presente no legado (paridade). |
| **Melhoria desejável** | Aumenta valor/UX/segurança sem mudar o “o quê” fundamental (modernização de produto). |
| **Débito legado** | Risco, workaround ou inconsistência conhecida (ex.: `GetAll().First()` para empresa). |
| **Sebrae** | Depende de contrato, dado ou política do ecossistema Sebrae. |
| **Multi-tenant real** | Necessário para isolamento e governança por empresa/tenant, além da mera coluna `IdEmpresa`. |

---

## 5. Regras consolidadas (referência)

Mapeamento rápido das RB da Fase 1 para linguagem de produto:

| ID | Em uma frase |
|----|----------------|
| RB-001 | Só entra na vitrine o que está publicado (`Status`). |
| RB-002 | O tipo do produto muda o fluxo (curso vs download vs consultoria etc.). |
| RB-003 | “Curso disponível” combina integração + filtros locais de data/status. |
| RB-004 | Pedido ganha código amigável único (8 caracteres, retry se colidir). |
| RB-005 | Sacola/pedido usa status ativo e remoção lógica; semântica varia por consulta. |
| RB-006 | Cupom válido por código, status e janela de datas. |
| RB-007 | Nova venda nasce como “paga” no modelo; itens vêm do pedido; pedido é removido após salvar venda. |
| RB-008 | E-mail pós-compra com assunto fixo “Sebrae”. |
| RB-009 | Inscrição paga envia venda ao financeiro Sebrae com constantes configuradas em código. |
| RB-010–012 | Consultoria, cliente e lead podem herdar **empresa errada** em base multi-empresa (primeira empresa do repositório). |
| RB-013 | Datas de cadastro/alteração preenchidas automaticamente na persistência. |

---

## 6. Glossário mínimo

| Termo | Definição (produto) |
|-------|---------------------|
| **Empresa** | Unidade de segregação de dados; proxy de **tenant** no legado (`IdEmpresa`). |
| **Produto** | Item comercializável; tipo define jornada (curso, evento, download, consultoria, pacote). |
| **Pedido** | Sacola / carrinho persistido com linhas (`ProdutoPedido`). |
| **Venda** | Registro de compra concluída, com itens (`ProdutoVenda`). |
| **Cupom** | Instrumento de desconto com validade e status. |
| **Integração / EventoCurso** | Dados de fronteira para disponibilidade de cursos no modelo atual. |
| **Loja (conceito)** | Conjunto de configurações e conteúdo por empresa (banners, menu, tema, FAQ, etc.) — não é tenant raiz separado (Fase 1). |

---

## 7. Limitações da Fase 1 a carregar para o produto

- A discovery em `.ai/outputs/01-discovery/` foi **alinhada** ao estado pós-restauração Git (controllers, `WebApiConfig`, `Metodos` Sebrae legíveis). Permanecem como trabalho: **auditoria** rota × auth × tenant e **build** da solução (ver `08-riscos-dependencias-e-duvidas.md`).
- **WinService**, **instalador** e comportamento completo de **Pagamento** não foram fechados na Fase 1 — permanecem **hipóteses ou discovery**.
- **Atomicidade** pedido → venda não confirmada na Fase 1 (RB-007).

---

## 8. Próximos artefatos (Fase 2)

| Arquivo | Conteúdo |
|---------|----------|
| [01-personas-e-atores.md](./01-personas-e-atores.md) | Quem usa o sistema. |
| [02-capacidades-e-casos-de-uso.md](./02-capacidades-e-casos-de-uso.md) | Capacidades e UC numerados. |
| [03-jornadas-principais.md](./03-jornadas-principais.md) | Jornadas ponta a ponta. |
| [04](./04-backlog-epicos.md)–[07](./07-backlog-tasks.md) | Backlog em quatro visões obrigatórias (tags). |
| [08](./08-riscos-dependencias-e-duvidas.md) | Riscos, dependências, dúvidas. |
| [09](./09-mvp.md) | MVP 1, MVP 2, pós-MVP, fora de escopo. |
