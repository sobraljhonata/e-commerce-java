# Jornadas principais — Fase 2

**Fontes:** Fase 1 `04-fluxos.md` (F0–F11), `03-regras-de-negocio.md`.  
Cada jornada liga **estados da UI** (AngularJS / UI-Router na Fase 1) a **capacidades** sem prescrever tecnologia alvo.

---

## J1 — Descoberta → detalhe (visitante)

**Objetivo:** Encontrar um produto adequado (curso/evento/conteúdo) e abrir a ficha.

**Etapas:**

1. Entrar na home (`/`) — vitrines e depoimentos (**F1**).
2. Opcional: filtrar por proximidade (`/api/produto/listarProximidade/{estado}`) (**F1**).
3. Navegar para cursos/eventos (`/cursos-eventos`) — listar e filtrar (**F2**).
4. Abrir detalhe (`/detalhe/:idProduto/:urlAmigavel`) — carregar produto e assets de parâmetros (**F3**).

**Regras de negócio relevantes:** RB-001 (publicação), RB-002 (tipo), RB-003 (disponibilidade de curso com integração).

**Pontos de dor / débito:** RB-003 acopla vitrine a dados Sebrae; em multi-tenant real, política de **qual** integração usar por empresa (tag `MT` + `SEB`).

**Saídas:** Produto escolhido para sacola ou para cadastro posterior.

---

## J2 — Compra ponta a ponta (cliente autenticado)

**Objetivo:** Concluir pedido com possível cupom e gerar venda.

**Etapas:**

1. Adicionar itens — persistência via `POST /api/pedido/salvar` (**F4**).
2. Remover item ou limpar sacola (**F4**).
3. Validar cupom `GET /api/cupom/validar-cupom/{codigo}` (**F4**, RB-006).
4. Opcional: vínculo de pessoas ao produto no pedido (**F4**).
5. Pagamento `POST /api/pedido/pagamento` (**F5**).
6. Efetivar venda `POST /api/pedido/efetuarvenda` (**F5**, RB-007).
7. Quando aplicável: aceitar termos `GET /api/venda/termoAceite/listar` (**F5**).
8. Quando aplicável: inscrição paga `POST /api/venda/efetuarInscricaoPaga` (**F5**, RB-009 — **Sebrae**).

**Regras:** RB-004 (código pedido), RB-005 (status sacola), RB-007 (venda e remoção pedido), RB-009 (financeiro Sebrae).

**Estados de tela (Fase 1):** `/sacola`, sucesso/falha de compra (`/compra-efetuada-com-sucesso`, `/compra-falhou/:Codigo`).

**Riscos:** atomicidade pedido→venda não confirmada (RB-007); e-mail pós-compra com copy Sebrae (RB-008).

---

## J3 — Cadastro e integração cadastral (cliente)

**Objetivo:** Criar acesso e completar dados PF/PJ com validações externas quando necessário.

**Etapas (amostra Fase 1 — F6):**

- `POST /api/user/login`, `loginsocial`
- `POST /api/usuario/Salvar`, `cadastraUserSebrae`
- Consultas `consultaPessoaFisicaIntegracao/{cpf}`, `consultaPessoaJuridicaIntegracao/{cnpj}`
- Fluxos de inscrição/reserva: `pessoaFisica/efetiva-inscricao`, `cancelarReservaEvento`

**Débito multi-tenant:** RB-011 — empresa padrão quando `IdEmpresa` vazio.

**Discovery:** política exata de autorização por rota no servidor não fechada na Fase 1 para todos os controllers.

---

## J4 — Pós-compra e relacionamento

**Objetivo:** Recuperar compras, baixar conteúdos adquiridos, gerenciar favoritos.

**Etapas (F7):**

- `GET /api/venda/obterporidcliente/{id}`
- `GET /api/venda/downloads/obterporidcliente/{id}`
- Operações em `/api/cliente/listadesejo/...`

**Regra:** RB-002 — downloads atrelados a tipo de produto.

---

## J5 — Consultoria via integração (deep link / fluxo dedicado)

**Objetivo:** Efetivar ou buscar consultoria e refletir no catálogo.

**Etapas (F9):**

- `POST /api/consultoriaIntegracao/efetivar`, `buscar`
- `POST /api/produto/salvarConsultoria`

**Débito:** RB-010 — empresa da consultoria pode ser a “primeira” do banco.

---

## J6 — Operação: sincronizar evento Sebrae com produto

**Objetivo:** Alinhar catálogo local a evento externo.

**Etapas (F10):**

- Front: `GET /api/produto/Sync/Eventos/{CodEvento}`
- Back: `ProdutoService.SincronizarIntegracaoEventos`

**Natureza:** fortemente **Sebrae** + risco de dados compartilhados em `Integracao` sem `IdEmpresa` na entidade `EventoCurso` (hipótese Fase 1).

---

## J7 — Institucional e confiança

**Objetivo:** Informação, dúvidas, contato.

**Etapas (F8):** HTML dinâmico, FAQ, Fale Conosco.

---

## J8 — Captura de lead

**Objetivo:** Registrar interesse para follow-up.

**Etapas (F11):** `POST /api/lead/salvar` — RB-012 (empresa e e-mail de destino).

---

## J9 — Entrada administrativa (host MVC)

**Objetivo:** Acessar área administrativa.

**Etapas (F0):** raiz MVC redireciona para `/admin`.

**Discovery:** mapa completo de telas admin não detalhado na Fase 1; capacidades inferidas pelos controllers da API.

---

## Resumo: jornada × épicos de backlog (referência)

| Jornada | Épicos principais (ver `04-backlog-epicos.md`) |
|---------|--------------------------------------------------|
| J1 | E-STORE, E-CAT |
| J2 | E-CART, E-CHK, E-PAY |
| J3 | E-IAM, E-CUST, E-INT-SEB |
| J4 | E-POST |
| J5–J6 | E-INT-SEB, E-CAT |
| J7–J8 | E-CMS, E-LEAD |
| J9 | E-ADM |
