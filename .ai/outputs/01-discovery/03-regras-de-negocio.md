# Catálogo de regras de negócio e comportamentos

**Atualização (2026-04-07, pós-restore Git):** o catálogo abaixo permanece baseado em **`MaisCode.Domain`**, **`MaisCode.App`** e **`MaisCode.Data`**. Os **controllers** e **`MaisCode.SebraeIntegracao/Metodos`** voltaram a ser legíveis no workspace; este arquivo **não** reenumera rotas HTTP — para isso ver `02-mapa-de-modulos.md`, `04-fluxos.md` e o código em `MaisCode.Site/Controllers/Api`.

Formato por item (conforme agente *Business Rules Miner*). Itens sem evidência explícita não são listados como “regra confirmada”.

---

### RB-001 — Publicação de produto

- **Regra:** `Status` em `Produto` representa publicação (“PUBLICAR” no comentário da entidade).
- **Contexto:** catálogo / vitrine.
- **Evidência:** comentário em `Produto.Status`; filtros `p.Status` em `ProdutoService` (ex.: `ListarCursosEventos`).
- **Impacto funcional:** itens inativos não entram nas listagens que filtram por `Status`.
- **Categoria:** regra de domínio (catálogo).
- **Nível de confiança:** média (comentário reforça; validação de invariantes não verificada na entidade).
- **Observações:** não há método de domínio; comportamento depende dos serviços.

---

### RB-002 — Tipos de produto

- **Regra:** Catálogo distingue tipos: Curso, Evento, Download, Consultoria, Pacote (`ETipoProduto`).
- **Contexto:** navegação, filtros, downloads, consultoria.
- **Evidência:** `MaisCode.Domain/Enum/ETipoProduto.cs`; uso em `Produto.TipoProduto`, `VendaService.ObterDownloadsPorIdCliente` (`ETipoProduto.Download`).
- **Impacto funcional:** fluxos diferentes por tipo (ex.: downloads após venda).
- **Categoria:** regra de domínio.
- **Nível de confiança:** alta.

---

### RB-003 — Curso “disponível” depende da integração + regras locais

- **Regra:** Listagem de cursos/eventos disponíveis exige `Integracao.EventoCurso.Situacao = 'Disponível'`, `prod.Status=1`, `TipoProduto` curso, `statusMigracao=0`; depois remove itens fora de `DisponivelVendaAte` ou data de evento passada.
- **Contexto:** vitrine de cursos.
- **Evidência:** `ProdutoService.ListarCursosEventosDisponiveis` — SQL e laço com `produtosRemove`.
- **Impacto funcional:** disponibilidade na loja acoplada ao **status vindo da integração** e a campos do produto.
- **Categoria:** mistura de **regra de negócio** (o que mostrar) + **comportamento Sebrae/integração** (tabela `Integracao.EventoCurso`).
- **Nível de confiança:** alta.

---

### RB-004 — Geração de código de pedido

- **Regra:** Ao salvar pedido sem `CodPedido`, gera string aleatória de 8 caracteres alfanuméricos; se colidir com pedido existente, tenta de novo recursivamente.
- **Contexto:** criação de sacola/pedido.
- **Evidência:** `PedidoService.Salvar`, `GeraCodPedido`.
- **Impacto funcional:** identificador amigável único (probabilístico).
- **Categoria:** detalhe técnico / regra operacional leve.
- **Nível de confiança:** alta.
- **Observações:** não há transação explícita citada; colisão tratada por retry.

---

### RB-005 — Pedido “ativo” vs remoção lógica

- **Regra:** Várias consultas filtram `p.Status` em pedidos; remoção usa `Status = false` e apaga itens de `ProdutoPedido`.
- **Contexto:** ciclo de vida da sacola.
- **Evidência:** `PedidoService.Listar`, `ObterPorId`, `Remover`.
- **Impacto funcional:** histórico pode incluir pedidos inativos dependendo do método (`ObterPorIdParaVendaCodPedido` não exige `Status`).
- **Categoria:** regra de domínio + detalhe de implementação.
- **Nível de confiança:** alta.

---

### RB-006 — Validação de cupom

- **Regra:** Cupom válido por código (uppercase), status diferente de `Deletado` e `Utilizado`, e data atual entre `InicioCupom` e `FimCupom` (comparação por `.Date`).
- **Contexto:** checkout.
- **Evidência:** `CupomService.ValidarCupom`.
- **Impacto funcional:** cupom fora da janela ou com status inadequado retorna null.
- **Categoria:** regra de domínio (promoção).
- **Nível de confiança:** alta.
- **Observações:** não analisado neste snapshot o vínculo cupom × produtos no carrinho (ficaria em outro método).

---

### RB-007 — Venda inicial e itens

- **Regra:** Ao salvar nova venda, define `Status = "1"` (comentário na entidade: "1" Pago); persiste itens de venda a partir dos produtos do pedido; em seguida chama remoção do pedido.
- **Contexto:** conclusão de compra.
- **Evidência:** `VendaService.Salvar` + comentários em `Venda.Status` (`MaisCode.Domain/Entity/Vendas/Venda.cs`).
- **Impacto funcional:** pedido é encerrado/removido após criar venda.
- **Categoria:** regra de domínio (transação comercial) + **lacuna**: ordem exata e consistência dependem de transação de BD não mostrada aqui.
- **Nível de confiança:** média-alta no fluxo; **baixa** para atomicidade.

---

### RB-008 — E-mail pós-compra com texto “Sebrae”

- **Regra:** Envio de e-mail de compra usa assunto **"Nova compra em Sebrae"** quando servidor configurado (Gmail/KingHost).
- **Contexto:** notificação.
- **Evidência:** `VendaService.EnviarEmailVenda`.
- **Impacto funcional:** branding e copy fixos ao Sebrae.
- **Categoria:** customização Sebrae (conteúdo).
- **Nível de confiança:** alta.

---

### RB-009 — Registro financeiro Sebrae no pagamento

- **Regra:** `EfetuarInscricaoPaga` monta payload (`VendasFinanceiro`, `ItensVendaFinanceiro`, `ContasFinanceiro`, `PagamantoFinanceiro`) com IDs fixos de forma de pagamento (`forma_pagamento_id = "133"`, `forma_pagamento_taxa_id = "1"`) e chama `FinanceiroService.SalvarVenda`.
- **Contexto:** inscrição paga / integração financeira.
- **Evidência:** `VendaService.EfetuarInscricaoPaga`; usings `MaisCode.SebraeIntegracao.Financeiro`.
- **Impacto funcional:** acoplamento a contrato e constantes do integrador Sebrae.
- **Categoria:** **comportamento Sebrae** + detalhe técnico (hardcode de IDs).
- **Nível de confiança:** alta no código do `VendaService`; **média-alta** sobre o endpoint HTTP chamado — `FinanceiroService.SalvarVenda` em `MaisCode.SebraeIntegracao/Metodos/FinanceiroService.cs` faz `POST` para `urlService + "financeiro/Vendas/postVendaEcommerce.json"` (comportamento sujeito a ambiente/`urlService`).

---

### RB-010 — Cadastro de consultoria atribui empresa arbitrária

- **Regra:** `SalvarConsultoria` define `IdEmpresa` com **primeira empresa** retornada por `_empresaRepository.GetAll().FirstOrDefault()`.
- **Contexto:** produto tipo consultoria vindo de integração (`ConsultoriaIntegraSvc` chama `/api/produto/salvarConsultoria`).
- **Evidência:** `ProdutoService.SalvarConsultoria`.
- **Impacto funcional:** em base multi-empresa, consultoria pode ser gravada no tenant **errado**.
- **Categoria:** **workaround legado / bug potencial** + impacto multi-tenant.
- **Nível de confiança:** alta.

---

### RB-011 — Preenchimento de `IdEmpresa` em cliente quando vazio

- **Regra:** Se `IdEmpresa` nulo ou `Guid.Empty` ao salvar cliente PF/PJ, preenche com empresa obtida via `GetAll().FirstOrDefault()`.
- **Contexto:** cadastro de cliente.
- **Evidência:** `ClientePessoaFisicaService` (~linhas 1155+), `ClientePessoaJuridicaService` (~402+); padrão semelhante em `EnderecoClientePessoaJuridicaService`.
- **Impacto funcional:** comportamento de **single-tenant implícito**.
- **Categoria:** workaround legado / risco multi-tenant.
- **Nível de confiança:** alta.

---

### RB-012 — Lead associado à primeira empresa

- **Regra:** Ao inserir novo lead, `IdEmpresa` é preenchido com `empresa.IdEmpresa` onde `empresa = _empresaRepository.GetAll().First()`; e-mail de notificação usa `empresa.Email` com assunto **"Novo Lead em Ecommerce"**.
- **Contexto:** captura de lead.
- **Evidência:** `LeadService.Salvar` (`MaisCode.App/Service/LeadService.cs`).
- **Impacto funcional:** mesmo risco de RB-011; notificação sempre para e-mail da “primeira” empresa.
- **Categoria:** workaround legado + conteúdo de e-mail genérico.
- **Nível de confiança:** alta.

---

### RB-013 — Timestamp automático

- **Regra:** Entidades com `DataCadastro` / `DataAlteracao` recebem valores em `SaveChanges`.
- **Contexto:** auditoria mínima.
- **Evidência:** `ApplicationDbContext.ConfigurarDataRegistro`.
- **Categoria:** detalhe técnico transversal.
- **Nível de confiança:** alta.

---

## Itens explicitamente **não** confirmados (hipóteses)

- **Política global** de autorização e validação de `IdEmpresa` **por rota**: controllers são legíveis, mas **não** há neste documento auditoria exaustiva de todos os `ApiController` — trabalho de segurança/QA. Verificar se `DashboardController` ainda mantém `[Authorize]` comentado no fonte atual.
- Regras completas de reserva/cancelamento de evento: endpoints no `UserSvc.js` — mapear para `UserController`/serviços e confirmar com testes.
- Escopo funcional completo de `MaisCode.Pagamento` e comportamento em produção do **Windows Service** — discovery (código presente; validação operacional pendente).
