# Resumo executivo — Fase 1 (Discovery)

**Escopo:** engenharia reversa do que está **presente e legível** no repositório `ECommerceSebrae`, alinhada aos agentes em `.ai/agents/phase-1-discovery` e contextos em `.ai/context`.

**Data de referência:** 2026-04-07.

### Notas de versão

- **2ª passagem:** descreveu o repositório quando **parte dos arquivos** (`.csproj`, controllers, `Metodos`, etc.) estava **corrompida** no disco (`0xFF`).
- **3ª passagem (alinhamento):** após `git checkout HEAD --` nos diretórios afetados, o workspace voltou a ter **fontes e projetos legíveis** para `MaisCode.Site`, `MaisCode.IoC`, `MaisCode.SebraeIntegracao` (incl. `Metodos/`), `MaisCode.Util`, `MaisCode.WinService`, `MaisCode.InstallService`, e demais projetos da solução restaurados na mesma operação. **Este resumo reflete o estado pós-restauração.** Detalhes históricos da corrupção permanecem arquivados na seção A de `07-lacunas-e-riscos.md`.

## Conclusões principais (com nível de confiança)

| Conclusão | Confiança |
|-----------|-----------|
| O núcleo de domínio e persistência permanece em `MaisCode.Domain`, `MaisCode.App` e `MaisCode.Data` (EF6, `ApplicationDbContext`, migrações). | **Alta** |
| Host **ASP.NET Web API 2** em `MaisCode.Site`: `Global.asax.cs` registra `WebApiConfig`; OWIN `Startup` + `Startup.Auth.cs` legíveis (OAuth `/token`, cookies, Google externo, etc.). | **Alta** |
| Rotas Web API: **`MapHttpAttributeRoutes()`** + rota default `api/{controller}/{id}` em `WebApiConfig.cs`; controllers usam **`[Route("api/...")]`** amplamente (ex.: `ProdutoController`). | **Alta** |
| **34** controllers em `Controllers/Api/*.cs` legíveis; alinhamento Angular ↔ API pode ser **confirmado** por leitura de C#, não só por nome de arquivo. | **Alta** |
| DI **Ninject** em `MaisCode.IoC/BootStrapper.cs` (repositórios, serviços) legível. | **Alta** |
| `MaisCode.SebraeIntegracao`: DTOs e **`Metodos/*.cs`** (ex.: `FinanceiroService.SalvarVenda` → `.../financeiro/Vendas/postVendaEcommerce.json`) **auditáveis** no código. | **Alta** |
| Projetos `MaisCode.Util`, `MaisCode.WinService`, `MaisCode.InstallService` **presentes** na árvore após restore. `.csproj` dos projetos restaurados com XML UTF-8 válido (BOM + `<?xml`). | **Alta** |
| A loja pública em **AngularJS** (`MaisCode.Ecommerce/AngularJS`), `/api/...` via `$rootScope.urlApi`. | **Alta** |
| **Empresa** (`IdEmpresa`) e riscos de **single-tenant** (`GetAll().First()`) no `MaisCode.App` **inalterados**. | **Alta** |
| Contexto do projeto cita **.NET Core 2.1**; o legado neste repo é **.NET Framework 4.6.1** + Web API 2 / OWIN. | **Alta** |

## Documentação gerada (navegação)

| Arquivo | Conteúdo |
|---------|----------|
| [01-inventario-tecnico.md](./01-inventario-tecnico.md) | Solução, projetos, stack, EF, limitações. |
| [02-mapa-de-modulos.md](./02-mapa-de-modulos.md) | Camadas, `MaisCode.Site`, módulos, rotas/DI. |
| [03-regras-de-negocio.md](./03-regras-de-negocio.md) | Catálogo RB (base App/Domain). |
| [04-fluxos.md](./04-fluxos.md) | Jornadas; contratos front + **evidência em controllers**. |
| [05-acoplamentos-sebrae.md](./05-acoplamentos-sebrae.md) | App/Domain + clientes em `Metodos`. |
| [06-analise-multi-tenant.md](./06-analise-multi-tenant.md) | `IdEmpresa`; **auditoria de API recomendada** rota a rota. |
| [07-lacunas-e-riscos.md](./07-lacunas-e-riscos.md) | Riscos; histórico de corrupção; lacunas remanescentes. |

## O que esta fase **não** fez

- Não desenhou arquitetura alvo.
- Não executou **build completo** nem testes E2E neste documento (recomendado validar localmente).
- Não inventoriou **todas** as rotas manualmente (gerar lista via Swashbuckle/reflexão é próximo passo operacional).

## Próximo passo recomendado

1. **Build** da solução + correção de dependências locais (pacotes NuGet).
2. **Inventário automático** de rotas Web API e cruzamento com `04-fluxos.md`.
3. **Auditoria multi-tenant** em controllers (filtro `IdEmpresa` vs contexto do usuário), rota a rota.
4. Rodar / documentar testes em `IntegraTestes` e `MaisCode.Pagamento`.
