# Análise multi-tenant — Fase 1 (atualizado pós-restore Git)

Respostas ao roteiro do agente *Tenant Model Analyst*, com evidências e limitações.

**Atualização:** os **controllers** em `MaisCode.Site/Controllers/Api` estão **legíveis**. Ainda **não** há, neste conjunto de documentos, uma **auditoria rota a rota** de `Authorize`, claims e validação de `IdEmpresa` — isso passa a ser trabalho factível por leitura de código + testes, não bloqueado por arquivo corrompido.

## 1. Empresa é tenant raiz?

- **Modelo de dados:** `Empresa` com `IdEmpresa` e ampla propagação de `IdEmpresa` em entidades de negócio (produto, pedido, venda, cupom, cliente, loja, lead, etc.). **Hipótese forte:** `Empresa` é o **tenant root** lógico.
- **Confiança:** alta para modelo; média para “tenant” como conceito de produto (não há nomenclatura `Tenant` no código).

## 2. Loja depende de Empresa?

- Não há entidade “Loja” separada como raiz; o que existe é conjunto de entidades `Loja/*` (**Banner**, **Menu**, **Tema**, **ParametroSite**, **HTMLPage**, **FAQ**, **FaleConosco**) com `IdEmpresa`.
- **Confiança:** alta — “loja” é **configuração e conteúdo por empresa**, não um tenant paralelo.

## 3. Segregação: schema, banco, coluna ou filtro lógico?

- **Coluna:** `IdEmpresa` em quase todas as tabelas de negócio relevantes (lista em inventário).
- **Schema único:** EF usa `Commerce` como padrão; integração em `Integracao`.
- **Sem evidência** neste código de multi-schema ou multi-banco por tenant.
- **Filtro global:** **não** encontrado `IDbCommandInterceptor` ou padrão de query filter centralizado no `ApplicationDbContext` analisado.
- **Confiança:** alta para coluna; alta para ausência de isolamento automático no EF neste arquivo.

## 4. O tenant afeta catálogo, preço, desconto, cliente e pedido?

| Área | Afetado por `IdEmpresa`? | Evidência |
|------|---------------------------|-----------|
| Catálogo / produto | Sim | `Produto.IdEmpresa` |
| Categoria / tema | Sim | `Categoria.IdEmpresa`, `TemaProduto`, `ProdutoTema` |
| Preço | Implícito (preço em `Produto` por linha) | `Produto.Valor` + `IdEmpresa` |
| Desconto | Sim | `Cupom.IdEmpresa`, `Campanha.IdEmpresa`, tabelas de junção |
| Cliente | Sim | `ClientePessoaFisica.IdEmpresa`, PJ, contatos, endereços |
| Pedido / venda | Sim | `Pedido`, `Venda`, `ProdutoPedido`, etc. |
| Parâmetros de site / e-mail / API keys | Sim | `ParametroSite.IdEmpresa` |
| Locale | Parcial | `Cidade`, `Endereco` com `IdEmpresa`; `Pais`/`Estado` **sem** `IdEmpresa` |

**Confiança:** alta.

## 5. Risco de vazamento entre tenants?

**Fatores de risco identificados (código):**

1. **`GetAll().First()` / `FirstOrDefault()` em `Empresa`** para definir `IdEmpresa` em fluxos de lead, consultoria, cadastro de cliente e endereço — comportamento de instância **single-tenant** disfarçado de modelo multi-tenant.
   - Arquivos: `LeadService.Salvar`, `ProdutoService.SalvarConsultoria`, `ClientePessoaFisicaService`, `ClientePessoaJuridicaService`, `EnderecoClientePessoaJuridicaService` (grep).
2. **Listagens sem filtro explícito por empresa** em alguns métodos de `ProdutoService` (ex.: `FiltrarCursosEventos` parte de `GetAllQueryAsync()` — depende do que o repositório expõe e de filtros aplicados). **Requer revisão linha a linha** com repositório e API.
3. **APIs — auditoria de isolamento pendente:** com os **fontes restaurados**, **cada** action deve ser revisada para garantir que leituras/escritas respeitam o tenant do usuário ou do contexto da loja (não assumir segurança só por `IdEmpresa` no modelo).

**Confiança:** alta para riscos (1); média para (2)-(3) até concluir a auditoria de API.

## 6. Escalabilidade do modelo atual

- **Prós:** coluna `IdEmpresa` permite sharding lógico e indexação simples.
- **Contras:** dados de integração (`Integracao`) aparentemente **compartilhados** entre tenants (não há `IdEmpresa` em `EventoCurso` no domínio lido); risco de colisão de `CodEvento` entre contextos — **hipótese média**, validar no DBA.
- **Contras:** hardcodes e integrações únicas (financeiro) dificultam N tenants com contratos distintos.

## 7. “Intenção SaaS” vs “realidade”

- **Intenção (modelo):** dados particionáveis por `IdEmpresa`.
- **Realidade (comportamento):** vários atalhos assumem **uma** empresa padrão; integração Sebrae é **central** e difícil de generalizar sem camada de adaptação.

## Recomendações (fase discovery — não implementação)

1. Inventariar **todas** as queries que usam `GetAll()` sem `IdEmpresa`.
2. Definir **contexto de tenant** na API (claim, header, host) e auditar correspondência com `IdEmpresa` gravado **em todas as rotas expostas**.
3. Tratar `Integracao.EventoCurso` como **dados de fronteira** com política de ownership explícita (coluna tenant ou staging por empresa).
4. Externalizar constantes financeiras e templates de e-mail por tenant.
