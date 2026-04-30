# Lacunas, riscos e hipóteses — Fase 1 (atualizado pós-restore Git)

## A. Histórico: corrupção de arquivos (`0xFF`) — **situação resolvida no workspace**

Entre a 1ª e a 2ª passagem da discovery, diversos arquivos `.csproj` e `.cs` no disco estavam preenchidos com byte **`0xFF`** (ilegíveis como texto/XML). Isso impedia auditar `WebApiConfig`, `BootStrapper`, `Startup.Auth`, controllers da API e `MaisCode.SebraeIntegracao/Metodos`.

**Ação tomada:** restauração a partir do Git com `git checkout HEAD --` nos diretórios afetados (`MaisCode.Site`, `MaisCode.IoC`, `MaisCode.SebraeIntegracao`, `MaisCode.Util`, `MaisCode.WinService`, `MaisCode.InstallService`, `MaisCode.Identity`, `MaisCode.Mail`, `MaisCode.Reports`, `MaisCode.Pagamento`, etc., conforme operação realizada no repositório).

**Estado atual (verificação típica):** cabeçalho de `MaisCode.Site.csproj` e correlatos com BOM UTF-8 e `<?xml`; fontes C# legíveis.

**Lição:** manter backup e validar integridade após cópias binárias; incluir checagem de build na CI.

---

## B. Lacunas factuais remanescentes

| Lacuna | Por que importa | Como reduzir |
|--------|-----------------|--------------|
| Auditoria **completa** de rotas Web API vs Angular | Garantir paridade e segurança | Gerar lista (Swashbuckle, reflexão ou script) e cruzar com `04-fluxos.md` |
| Política de **tenant** em cada endpoint | Risco de vazamento multi-empresa | Code review sistemático + testes com dois tenants |
| Testes `IntegraTestes` e cenários Pagamento | Regressão de integrações | Executar solução + documentar casos |
| Drift BD vs migrações EF | Dados reais ≠ modelo | Comparar schema produzido com migrations |
| Arquivos **não rastreados** em `MaisCode.Pagamento` | Podem quebrar build local | Revisar `git status`; remover ou adicionar ao Git |
| `git status` com deleções em `MaisCode.Ecommerce` | UI/asset incompleto | Restaurar pasta se necessário para loja funcional |

---

## C. Riscos técnicos e de negócio

1. **Multi-tenant inconsistente** (`GetAll().First()` para `IdEmpresa`) — **alto** (`MaisCode.App`).
2. **Dependência Sebrae** em catálogo e financeiro — **alto**.
3. **Hardcodes financeiros** em `VendaService.EfetuarInscricaoPaga` — **alto**.
4. **OAuth / HTTP inseguro em dev:** `AllowInsecureHttp = true` em `Startup.Auth.cs` — **médio** em produção se não corrigido.
5. **`[Authorize]` comentado** em alguns controllers (ex.: histórico do `DashboardController`) — **médio/alto**; **revalidar** no fonte atual.
6. **Stack legada** (.NET 4.6.1, EF6, AngularJS 1.x) — **médio** (modernização fora do escopo desta fase).
7. **Interceptor HTTP** no Angular (`siteConfig.js`) — **médio** (possível conflito de headers).

---

## D. Hipóteses (não confirmadas)

| ID | Hipótese | Motivo de não confirmar |
|----|----------|-------------------------|
| H1 | Existe outro repositório com **.NET Core 2.1** citado no contexto | Este repo permanece .NET Framework + Web API 2 |
| H2 | `CodEvento` é único globalmente em `Integracao.EventoCurso` | BD não inspecionado |
| H3 | Perfil + claim resolvem tenant do usuário em runtime | Exige mapeamento completo token ↔ empresa em todas as rotas |
| H4 | `winService` executa sincronização batch | Código presente; agendamento/função exata — confirmar com ops |
| H5 | Maioria das actions usa attribute routing explícita | Há `MapHttpAttributeRoutes` + muitos `[Route]`; ainda há fallback `api/{controller}/{id}` |

---

## E. Fatos confirmados (referência rápida — pós-restore)

- `WebApiConfig`: attribute routes + `api/{controller}/{id}`, CORS `*`, JSON only.
- `Startup.Auth`: OAuth bearer `/token`, Identity, cookies, Google externo (rever secrets e HTTPS).
- `BootStrapper`: Ninject com `ApplicationDbContext` e repositórios/serviços.
- `ProdutoController` e demais API controllers: **legíveis**; rotas alinháveis ao Angular.
- `FinanceiroService` e outros em `Metodos/`: **legíveis**; URLs relativas a `urlService`.
- `MaisCode.Util`, `WinService`, `InstallService`: presentes na árvore após restore.

---

## F. Próximas ações sugeridas

1. **Build** completo + restaurar pacotes NuGet.
2. Inventário de rotas + matriz **rota × auth × tenant**.
3. Entrevista com time: quantas empresas em produção e política de `IdEmpresa`.
4. Opcional: atualizar **Fase 2** (`.ai/outputs/02-product/08-riscos-dependencias-e-duvidas.md`) removendo alertas de “discovery obsoleta por corrupção” onde já refletido.
