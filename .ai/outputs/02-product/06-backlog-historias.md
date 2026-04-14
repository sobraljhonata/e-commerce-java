# Backlog — Histórias de usuário — Fase 2

**Formato:** Como **&lt;ator&gt;**, quero **&lt;capacidade&gt;**, para **&lt;valor&gt;**.  
**CA** = critérios de aceitação (testáveis em linguagem de negócio).  
**Refs:** UC (caso de uso em `02-capacidades-e-casos-de-uso.md`), RB (Fase 1).

Cada história carrega tags: **PARC / MOD / SEB / MT** (uma ou mais).

---

## Storefront e catálogo

### US-001 — Vitrine inicial
**Como** visitante, **quero** ver na home cursos, eventos, downloads e consultorias em destaque, **para** descobrir ofertas rapidamente.  
**Tags:** PARC  
**Refs:** UC-01, F1  
**CA:**
1. A home apresenta blocos de conteúdo equivalentes aos listados na Fase 1 (próximos eventos, cursos, downloads, consultorias).
2. Depoimentos podem ser exibidos na home.
3. Se a proximidade por estado for usada, o visitante consegue informar ou derivar estado e ver listagem correspondente.

### US-002 — Buscar cursos e eventos
**Como** visitante, **quero** filtrar cursos/eventos por critérios como nome, categoria, cidade e datas, **para** achar um produto adequado.  
**Tags:** PARC, SEB  
**Refs:** UC-02, RB-003  
**CA:**
1. É possível listar e filtrar conforme metadados citados na Fase 1.
2. Para cursos, a regra de “disponível” reflete RB-003 (integração + filtros locais) **ou** está documentada como comportamento provisório até desacoplamento.
3. Produtos inativos não aparecem nas listagens que dependem de `Status` (RB-001).

### US-003 — Ver ficha do produto
**Como** visitante, **quero** ver detalhes, preço e mídias do produto, **para** decidir a compra.  
**Tags:** PARC  
**Refs:** UC-04, F3  
**CA:**
1. A ficha carrega dados do produto e conteúdos auxiliares de parâmetros de site quando aplicável.
2. O tipo do produto (curso, evento, download, consultoria, pacote) é visível ou inferível na experiência (RB-002).

### US-004 — Sincronizar produto com evento externo
**Como** gestor, **quero** acionar sincronização por código de evento, **para** alinhar o catálogo ao evento Sebrae.  
**Tags:** PARC, SEB, MT  
**Refs:** UC-20, F10  
**CA:**
1. A operação aceita identificador de evento como na Fase 1.
2. O resultado da sincronização é comunicado ao gestor (sucesso/erro em linguagem de negócio).
3. **Hipótese:** impacto multi-empresa em `Integracao.EventoCurso` documentado — se não houver segregação, registrar risco (Fase 1).

---

## Sacola, promoção, checkout

### US-010 — Manter sacola
**Como** cliente autenticado, **quero** adicionar e remover itens e esvaziar a sacola, **para** organizar minha compra.  
**Tags:** PARC, MT  
**Refs:** UC-05, RB-004, RB-005  
**CA:**
1. Pedido recebe código amigável quando ausente (RB-004); colisão é tratada sem expor erro técnico ao usuário.
2. Remover item e limpar sacola refletem RB-005.
3. Itens só referenciam produtos da empresa correta do contexto (requisito MT — pode iniciar como débito documentado).

### US-011 — Usar cupom
**Como** cliente, **quero** aplicar um cupom válido, **para** obter desconto.  
**Tags:** PARC  
**Refs:** UC-06, RB-006  
**CA:**
1. Cupom fora da janela ou com status inválido é recusado com mensagem clara.
2. Código tratado de forma case-insensitive conforme serviço (uppercase na Fase 1).

### US-012 — Pagar e concluir compra
**Como** cliente, **quero** pagar e finalizar a compra, **para** receber meus produtos/serviços.  
**Tags:** PARC, SEB  
**Refs:** UC-07, RB-007, RB-009  
**CA:**
1. Após sucesso, existe registro de venda coerente com itens do pedido (RB-007).
2. O pedido deixa de ser a sacola ativa conforme regra legada.
3. Quando aplicável, inscrição paga dispara integração financeira (RB-009) com parâmetros rastreáveis por empresa (evolução MT).

### US-013 — Aceitar termos obrigatórios
**Como** cliente, **quero** ver e aceitar termos quando exigidos, **para** cumprir requisitos legais/comerciais.  
**Tags:** PARC  
**Refs:** UC-08, F5  
**CA:**
1. Termos disponíveis antes da conclusão quando o negócio exigir.
2. O aceite fica associado à venda de forma auditável (nível de detalhe a confirmar com jurídico — discovery).

---

## Conta, cliente, integração

### US-020 — Entrar na loja
**Como** cliente, **quero** autenticar-me (incluindo opção social quando existir), **para** acessar área logada.  
**Tags:** PARC, MOD  
**Refs:** UC-09, F6  
**CA:**
1. Login com credenciais válidas concede sessão.
2. Fluxo social equivalente ao citado no front (detalhes — discovery se política servidor não mapeada).

### US-021 — Cadastrar-se
**Como** novo usuário, **quero** criar cadastro, **para** comprar e gerenciar conta.  
**Tags:** PARC, MT  
**Refs:** UC-10, F6  
**CA:**
1. Cadastro persiste usuário conforme regras de negócio vigentes.
2. O contexto de empresa do cadastro é explícito — não depende de “primeira empresa” (evolução MT; paridade pode documentar exceção RB-011).

### US-022 — Manter cliente PF/PJ
**Como** cliente, **quero** manter meus dados de cliente PF ou PJ, **para** usar em compras e documentos.  
**Tags:** PARC, SEB, MT  
**Refs:** UC-11, RB-011  
**CA:**
1. Dados persistem com `IdEmpresa` coerente com o tenant da sessão (meta MT).
2. Integrações de consulta CPF/CNPJ funcionam quando configuradas (UC-12).

### US-023 — Inscrição e reserva de evento
**Como** cliente, **quero** efetivar ou cancelar inscrição/reserva quando o produto exigir, **para** participar do evento.  
**Tags:** PARC, SEB  
**Refs:** UC-13, F6  
**CA:**
1. Endpoints equivalentes aos citados no `UserSvc.js` têm comportamento confirmado com time (discovery Fase 1).
2. Erros de integração retornam mensagem acionável, não apenas código técnico.

---

## Pós-compra e engajamento

### US-030 — Ver compras e downloads
**Como** cliente, **quero** ver minhas vendas e baixar conteúdos adquiridos, **para** consumir o que comprei.  
**Tags:** PARC  
**Refs:** UC-14, RB-002  
**CA:**
1. Lista de vendas por cliente disponível.
2. Downloads só para produtos do tipo adequado (RB-002).

### US-031 — Lista de desejos
**Como** cliente, **quero** salvar produtos favoritos, **para** decidir depois.  
**Tags:** PARC  
**Refs:** UC-15, F7  
**CA:**
1. Incluir, listar e remover favoritos conforme contrato atual.

---

## Institucional e lead

### US-040 — Conteúdo institucional
**Como** visitante, **quero** ler páginas institucionais, FAQ e enviar contato, **para** confiar e tirar dúvidas.  
**Tags:** PARC, MT  
**Refs:** UC-17, UC-18  
**CA:**
1. HTML dinâmico por nome/id conforme Fase 1.
2. FAQ listável.
3. Fale Conosco registra solicitação; **conteúdo** parametrizável por empresa quando aplicável (MT).

### US-041 — Registrar interesse (lead)
**Como** visitante, **quero** deixar meu contato, **para** ser respondido pela empresa certa.  
**Tags:** PARC, MT, DEB  
**Refs:** UC-16, RB-012  
**CA:**
1. Lead armazenado com empresa do contexto atual — não a “primeira” do sistema (correção MT).
2. Notificação enviada ao e-mail configurado **da mesma empresa**.

---

## Consultoria

### US-050 — Fluxo de consultoria integrada
**Como** cliente ou operador do fluxo, **quero** efetivar/buscar consultoria e gravar produto, **para** concluir a contratação.  
**Tags:** PARC, SEB, MT, DEB  
**Refs:** UC-19, RB-010  
**CA:**
1. Fluxo equivalente a `ConsultoriaIntegraSvc` (Fase 1).
2. Produto consultoria associado à empresa correta (correção RB-010).

---

## Administração

### US-060 — Ver dashboard operacional
**Como** gestor, **quero** ver indicadores resumidos, **para** acompanhar a operação.  
**Tags:** PARC, MOD  
**Refs:** UC-23  
**CA:**
1. Painel apresenta os dados que o negócio definiu como mínimos (paridade com `ObterDados` da Fase 1 + evolução por workshop).

### US-061 — Relatórios
**Como** gestor, **quero** extrair relatórios comerciais, **para** análise e auditoria.  
**Tags:** PARC  
**Refs:** UC-24  
**CA:**
1. Escopo exato dos relatórios existentes — **discovery** com base em `ReportController` e telas admin.

### US-062 — Configurar loja (aparência e conteúdo)
**Como** gestor, **quero** configurar banners, menus, temas e parâmetros, **para** alinhar a loja à minha empresa.  
**Tags:** PARC, MT  
**Refs:** UC-21  
**CA:**
1. Alterações refletem apenas na empresa do gestor.
2. Paridade com capacidades `Loja/*` da Fase 1.

### US-063 — Upload de arquivos
**Como** gestor, **quero** enviar arquivos necessários à operação, **para** atualizar a loja/conteúdo.  
**Tags:** PARC  
**Refs:** UC-25  
**CA:**
1. Tipos de arquivo e tamanhos máximos definidos por política de negócio (discovery técnico).

---

## Integração e modernização transversal

### US-070 — Desacoplar disponibilidade de curso da vitrine
**Como** produto, **quero** que a vitrine consuma um serviço de catálogo neutro, **para** trocar a fonte Sebrae sem reescrever a loja.  
**Tags:** MOD, SEB, MT  
**Refs:** RB-003, V3  
**CA:**
1. Regras de “disponível” podem ser simuladas com fonte mock/stub em homologação.
2. Configuração por empresa define se usa integração Sebrae ou não.

### US-071 — Parametrizar integração financeira
**Como** financeiro, **quero** configurar IDs de forma de pagamento e taxas por empresa/ambiente, **para** não depender de código fixo.  
**Tags:** SEB, MT, MOD  
**Refs:** RB-009  
**CA:**
1. Nenhum ID de forma de pagamento obrigatório em código de aplicação.
2. Alteração de configuração não exige deploy (meta de produto).

### US-072 — Observabilidade mínima das jornadas críticas
**Como** operador, **quero** saber se checkout e integrações estão falhando, **para** agir antes do cliente abandonar.  
**Tags:** MOD  
**Refs:** E-OBS, `.ai/context/02-modernization-goals.md`  
**CA:**
1. Indicadores acordados com o negócio (taxa de falha de checkout, fila de integração pendente, etc.) ficam visíveis em painel ou relatório operacional — **sem** definir ferramenta nesta fase.

---

## Pagamento (discovery)

### US-080 — Esclarir pagamentos existentes no legado
**Como** time, **quero** documentar o papel do projeto de pagamento legado, **para** decidir paridade no MVP.  
**Tags:** PARC  
**Refs:** UC-D2, Fase 1 lacunas  
**CA:**
1. Documento de negócio descreve quais fluxos usam adquirente vs integração Sebrae.

---

## Resumo: histórias por visão obrigatória

| Visão | Histórias principais |
|-------|----------------------|
| **Paridade** | US-001–004, 010–013, 020–023, 030–031, 040–041, 050, 060–063, 080 |
| **Modernização** | US-020, 060, 070–072 |
| **Sebrae** | US-002, 004, 012, 022–023, 050, 070–071 |
| **Multi-tenant** | US-004, 010, 021–022, 040–041, 050, 062, 071 |
