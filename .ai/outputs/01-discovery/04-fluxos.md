# Mapa de fluxos — Fase 1 (atualizado pós-restore Git)

**Premissa:** as rotas consumidas pela loja continuam documentadas a partir dos serviços AngularJS (`MaisCode.Ecommerce/AngularJS/Svc/*.js`). Após **restauração do Git**, os controllers em **`MaisCode.Site/Controllers/Api/*.cs`** estão **legíveis**; o roteamento usa **`MapHttpAttributeRoutes()`** e rotas `[Route("api/...")]` (além do template default `api/{controller}/{id}` em `WebApiConfig.cs`). A correspondência Angular ↔ servidor passa a ser **confirmável** por inspeção de C#, não só inferida por nome de arquivo.

**Exemplo de evidência:** `ProdutoController` declara explicitamente rotas como `api/produto/listarProxEventos`, `api/produto/Sync/Eventos/{PrdSync}`, etc., alinhadas aos fluxos abaixo.

## Convenções

- **Base URL:** `$rootScope.urlApi` (ex.: `http://localhost:58247`) — `siteApp.js`.
- **Auth:** Bearer token no interceptor quando `localStorage.access_token` existe.

---

## Fluxo F1 — Navegação e vitrine (home / listagens)

- **Nome:** Vitrine inicial e blocos de destaque.
- **Objetivo:** Exibir próximos eventos, cursos, downloads e consultorias na home.
- **Atores:** Visitante.
- **Entradas:** estado/região (opcional, fluxo de proximidade).
- **Passos:** Angular chama `GET .../api/produto/listarProxEventos`, `ListarCursosHome`, `ListarDownloadsHome`, `ListarConsultoriaHome`, `GET .../api/depoimento/listarHome`, `GET .../api/produto/listarProximidade/{estado}`.
- **Alternativas:** Depoimentos página completa: `listar`.
- **Entidades:** `Produto`, `Depoimento`.
- **Regras:** filtros por `Status` e tipo no backend (não inspecionado aqui além de `ProdutoService`).
- **Dependências externas:** nenhuma obrigatória no front.
- **Evidência:** `HomeSvc.js`.

---

## Fluxo F2 — Catálogo cursos/eventos e busca

- **Nome:** Listar e filtrar cursos/eventos.
- **Objetivo:** Apresentar catálogo e aplicar filtros (nome, categoria, cidade, datas, ordenação).
- **Atores:** Visitante.
- **Passos:** `GET /api/produto/listarCursosEventos`; `POST /api/produto/filtrarCursosEventos/`; `POST /api/produto/listarCursosEventosFiltroPadrao`; metadados `categoria`, `cidade`, `estado`, `temaproduto`.
- **Entidades:** `Produto`, `Categoria`, `Cidade`, `Estado`, `TemaProduto`.
- **Regras:** RB-003 (disponibilidade com `Integracao.EventoCurso`) quando lista “disponíveis” for usada no backend correspondente.
- **Evidência:** `CursosEventosSvc.js`, `ProdutoService.ListarCursosEventosDisponiveis`.

---

## Fluxo F3 — Detalhe do produto

- **Nome:** Página de detalhe.
- **Objetivo:** Carregar produto para exibição rica (loja) e assets de parâmetros do site.
- **Passos:** `GET /api/produto/obterporid/loja/{id}`; `GET /api/produto/obter-por-categoria-ou-tema/{id}`; `GET /api/Loja/parametroSite/obterImagensCurso/{idPortifolio}`, `obterFrasedestaque`, `obterImagensFormato/{formato}`.
- **Entidades:** `Produto`, `ParametroSite`.
- **Evidência:** `DetalheSvc.js`.

---

## Fluxo F4 — Sacola (pedido) e cupom

- **Nome:** Carrinho e validação de cupom.
- **Objetivo:** Manter pedido ativo, linhas e cupom.
- **Atores:** Cliente autenticado (implícito em várias telas).
- **Passos:** `POST /api/pedido/salvar`; `GET /api/produto-pedido/delete/{idPedido}/{idProduto}`; `GET /api/pedido/limpar-sacola/{idPedido}`; `GET /api/cupom/validar-cupom/{codigo}`; `POST /api/pedido/vincular-pessoas-produto-pedido`.
- **Entidades:** `Pedido`, `ProdutoPedido`, `Cupom`.
- **Regras:** RB-004, RB-005, RB-006.
- **Evidência:** `PedidoSvc.js`, `PedidoService`, `CupomService`.

---

## Fluxo F5 — Pagamento e venda

- **Nome:** Checkout e conclusão.
- **Objetivo:** Processar pagamento, criar venda, termos, integração financeira Sebrae.
- **Passos:** `POST /api/pedido/pagamento`; `POST /api/pedido/efetuarvenda`; `GET /api/venda/termoAceite/listar`; `POST /api/venda/efetuarInscricaoPaga`.
- **Entidades:** `Pedido`, `Venda`, `ProdutoVenda`, `TermoAceite`, `CupomUso` (opcional).
- **Regras:** RB-007, RB-009.
- **Dependências externas:** `MaisCode.SebraeIntegracao` financeiro (`VendaService` → `FinanceiroService.SalvarVenda` em `Metodos/FinanceiroService.cs`, `POST` relativo a `financeiro/Vendas/postVendaEcommerce.json`).
- **Evidência:** `PedidoSvc.js`, `VendaService.cs`.

---

## Fluxo F6 — Conta, cadastro, integração cadastral Sebrae

- **Nome:** Identidade e cadastro PF/PJ.
- **Objetivo:** Login, cadastro, sincronizar com integrações Sebrae, reservas/inscrições.
- **Passos (amostra):** `POST /api/user/login`, `loginsocial`; `POST /api/usuario/Salvar`, `cadastraUserSebrae`, `consultaPessoaFisicaIntegracao/{cpf}`, `consultaPessoaJuridicaIntegracao/{cnpj}`, `pessoaFisica/efetiva-inscricao`, `cancelarReservaEvento`, etc.
- **Entidades:** `ApplicationUser`, `ClientePessoaFisica`, `ClientePessoaJuridica`.
- **Regras:** RB-011; demais regras **não** lidas sem código do controller.
- **Dependências externas:** `MaisCode.SebraeIntegracao` (usings no App; clientes em `Metodos/*.cs` **legíveis** pós-restore).
- **Evidência:** `UserSvc.js`, usings nos serviços de cliente/usuário.

---

## Fluxo F7 — Pós-compra (histórico, downloads, lista de desejos)

- **Nome:** Área logada pós-venda.
- **Objetivo:** Listar vendas, downloads, favoritos.
- **Passos:** `GET /api/venda/obterporidcliente/{id}`; `GET /api/venda/downloads/obterporidcliente/{id}`; `GET/POST` em `/api/cliente/listadesejo/...`.
- **Entidades:** `Venda`, `ProdutoVenda`, `ClienteListaDesejo`.
- **Evidência:** `UserSvc.js`.

---

## Fluxo F8 — Conteúdo institucional e FAQ

- **Nome:** Páginas HTML dinâmicas, FAQ, Fale Conosco.
- **Passos:** `GET /api/htmlPage/obterpornome/{nome}`, `obterporid/{id}`; `GET /api/loja/faq/listar`.
- **Entidades:** `HTMLPage`, `Faq`, `FaleConosco`.
- **Evidência:** `FaleConoscoSvc.js`, `DuvidasFrequentesSvc.js`, `LandingPageSvc.js`.

---

## Fluxo F9 — Consultoria (integração)

- **Nome:** Efetivação / busca de consultoria e gravação de produto.
- **Passos:** `POST /api/consultoriaIntegracao/efetivar`, `buscar`; `POST /api/produto/salvarConsultoria`.
- **Regras:** RB-010 (empresa = primeira do repositório).
- **Evidência:** `ConsultoriaIntegraSvc.js`, `ProdutoService.SalvarConsultoria`.

---

## Fluxo F10 — Sincronização de evento (admin/operacional)

- **Nome:** Sync de produto com evento Sebrae.
- **Passos:** `GET /api/produto/Sync/Eventos/{CodEvento}` (front); lógica em `ProdutoService.SincronizarIntegracaoEventos` (back).
- **Entidades:** `Produto`, `EventoCurso` (`Integracao`).
- **Evidência:** `PedidoSvc.js`, `ProdutoService.cs`.

---

## Fluxo F11 — Lead

- **Nome:** Captura de lead.
- **Passos:** `POST /api/lead/salvar`.
- **Regras:** RB-012.
- **Evidência:** `LeadSvc.js`, `LeadService.cs`.

---

## Mapa rápido UI-Router ↔ API (referência)

Principais estados em `siteConfig.js`: `/`, `/cursos-eventos`, `/conteudos`, `/detalhe/:idProduto/:urlAmigavel`, `/sacola`, `/login`, `/cadastro`, `/compra-efetuada-com-sucesso`, `/compra-falhou/:Codigo`, `/gerenciamento-consultoria/...`, `/imprimir-recibo/:IdVenda`, etc.

**Confiança:** alta para rotas front; **alta** para correspondência com actions Web API onde `[Route]` coincide com o Angular (validação exaustiva = tarefa de inventário); **média** onde só vale o template `api/{controller}/{id}` sem attribute route explícita no método.

---

## Fluxo F0 — Admin / host MVC (observação)

- **Nome:** Entrada MVC do site.
- **Passos:** `GET /` (ou home MVC) redireciona para `/admin`.
- **Evidência:** `MaisCode.Site/Controllers/HomeController.cs`.
- **Confiança:** alta.
