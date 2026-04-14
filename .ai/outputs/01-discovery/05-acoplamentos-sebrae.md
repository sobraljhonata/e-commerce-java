# Análise de acoplamentos Sebrae — Fase 1 (atualizado pós-restore Git)

Classificação conforme agente *Sebrae Decoupling Analyst*.

**Estado atual:** `MaisCode.SebraeIntegracao` inclui DTOs (ex.: `Financeiro/ParametroVenda.cs`) e a pasta **`Metodos/`** com clientes HTTP **legíveis** (ex.: `FinanceiroService`, `EventosService`, `IntegraSebrae` base). Endpoints relativos (ex.: `.../financeiro/Vendas/postVendaEcommerce.json`) e uso de `HttpClient` podem ser auditados no código; **comportamento em runtime** (auth, retries, ambientes) ainda depende de configuração e testes integrados.

| Item | Onde aparece | Impacto | Criticidade | Estratégia sugerida |
|------|----------------|---------|-------------|---------------------|
| **Campos de produto ligados a evento/situação Sebrae** (`idEventoSebrae`, `SituacaoSebrae`, `statusMigracao`, `EventoLocal`, `Regional`, `Projeto`, `Acao`, etc.) | `MaisCode.Domain/Entity/Produtos/Produto.cs`; queries em `ProdutoService` | Modelo de catálogo não é neutro | **Alta** | **Isolar** em anti-corrupção (adapter) ou metadados extensíveis |
| **Tabela `Integracao.EventoCurso`** | `EventoCursoConfiguration`; SQL em `ProdutoService.ListarCursosEventosDisponiveis` | Disponibilidade de cursos depende do pipeline Sebrae | **Alta** | **Isolar** leitura; **parametrizar** fonte por tenant |
| **Entidade `EventoCurso`** | `MaisCode.Domain/Entity/Integracao/EventoCurso.cs` | Dados organizacionais Sebrae no BD | **Alta** | Tratar como **dados de fronteira** / ETL |
| **Cliente / usuário — APIs Sebrae** | `ClientePessoaFisicaService`, `ClientePessoaJuridicaService`, `UsuarioService` (`using MaisCode.SebraeIntegracao.*`) | Cadastro e sincronização dependem do barramento Sebrae | **Alta** | **Isolar** em bounded context “CRM/Parceiros” |
| **CEP / endereço via serviço Sebrae** | Referências a `EnderecoServiceSebrae` no App | Dependência de serviço Sebrae | **Média** | **Parametrizar** provedor; fallback neutro |
| **Financeiro — `VendasFinanceiro` / `FinanceiroService`** | `VendaService.EfetuarInscricaoPaga`; DTO `ParametroVenda` legível em `MaisCode.SebraeIntegracao/Financeiro/ParametroVenda.cs` | Pagamento inscrito no ecossistema financeiro Sebrae | **Crítica** | **Isolar** + **parametrizar** IDs por tenant/ambiente |
| **Hardcode de forma de pagamento** (`forma_pagamento_id = "133"`, taxa `"1"`) | `VendaService.EfetuarInscricaoPaga` | Fragilidade operacional | **Alta** | **Parametrizar** em banco/config por empresa |
| **URLs e copy “Sebrae”** | `UsuarioService`, `VendaService.EnviarEmailVenda` | Multi-marca difícil | **Média** | Templates por `ParametroSite` / tenant |
| **Assets “Loja-Sebrae-Mais-Code”** | Referências no front/projeto Ecommerce | Branding | **Baixa** técnica | Tema por tenant |
| **`siteApp.js` — URL comentada** | `http://sebrae.maiscode.com.br` | Configuração de ambiente | **Baixa** | Build/config por ambiente |

## Itens que são integração mas não “regra Sebrae”

- **SMTP** via `MaisCode.Mail` — projeto restaurado; implementação **auditável** no código-fonte atual (detalhes de provedor e configuração em `App.config`/código).

## Confiança global desta seção

- **Alta** para itens com código em `MaisCode.Domain` / `MaisCode.App` citado acima.
- **Média-alta** para **URLs e formato** das chamadas em `Metodos/*.cs` (código legível); **média** para semântica completa de erro, autenticação e SLAs sem testes contra ambiente Sebrae.
- **Alta** para estrutura de payload financeiro (DTOs + serialização em `FinanceiroService`).
