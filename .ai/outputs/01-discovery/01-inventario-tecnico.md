# Inventário técnico — Fase 1 (atualizado pós-restore Git)

**Alinhamento:** em 2026-04-07 o workspace foi **restaurado** a partir de `git checkout HEAD --` nos projetos que haviam sido corrompidos ou estavam ausentes. Este inventário descreve o **estado atual** do disco; a **2ª passagem** que listava `.csproj`/`*.cs` como `0xFF` está **obsoleta** para esses caminhos (ver histórico em `07-lacunas-e-riscos.md` §A).

## 1. Discrepância de stack (importante)

- **Contexto do projeto** (`.ai/context/00-project-context.md`) indica API **.NET Core 2.1**.
- **Evidência no código:** `MaisCode.App`, `MaisCode.Data`, `MaisCode.Domain`, `MaisCode.Ecommerce`, `MaisCode.CupomSvc` usam **TargetFrameworkVersion v4.6.1**, **Entity Framework 6.1.3**, **ASP.NET MVC 5.2.5** no `MaisCode.Ecommerce`.
- **`MaisCode.Site`:** **ASP.NET Web API 2** sobre .NET Framework; `WebApiConfig.cs` com `MapHttpAttributeRoutes()` e rota `api/{controller}/{id}`; CORS amplo; `HostAuthenticationFilter(OAuthDefaults.AuthenticationType)`.
- **Confiança:** alta. **Hipótese:** outro repositório/branch contém Core, ou o contexto descreve alvo futuro.

## 2. Solução e presença de projetos

Arquivo `MaisCode.sln` (Visual Studio 2017) referencia, entre outros:

`MaisCode.Domain`, `MaisCode.Reports`, `MaisCode.Mail`, `MaisCode.Identity`, `MaisCode.Data`, `MaisCode.IoC`, `MaisCode.Util`, `MaisCode.App`, `MaisCode.Site`, `MaisCode.Ecommerce`, `MaisCode.SebraeIntegracao`, `MaisCode.IntegraTestes`, `MaisCode.Pagamento`, `winService`, `InstalarServico`.

### 2.1 Pastas no workspace vs solução (pós-restore)

| Projeto (sln) | Pasta presente? | `.csproj` legível (XML)? | Observação |
|---------------|-----------------|---------------------------|------------|
| MaisCode.Domain | Sim | Sim | Núcleo estável. |
| MaisCode.App | Sim | Sim | Referencia Mail, SebraeIntegracao, Util. |
| MaisCode.Data | Sim | Sim | |
| MaisCode.Ecommerce | Sim | Sim | Dependência na sln sobre `MaisCode.Site`. |
| MaisCode.CupomSvc | Sim | Sim | |
| MaisCode.Site | Sim | Sim | Controllers API, `WebApiConfig`, `Startup.Auth` legíveis. |
| MaisCode.IoC | Sim | Sim | `BootStrapper.cs` legível. |
| MaisCode.SebraeIntegracao | Sim | Sim | Inclui `Metodos/*.cs` legíveis. |
| MaisCode.IntegraTestes | Sim | Sim | |
| MaisCode.Pagamento | Sim | Sim | Alguns `.cs` podem ser **não rastreados** no Git (ver §2.2). |
| MaisCode.Identity | Sim | Sim | |
| MaisCode.Mail | Sim | Sim | |
| MaisCode.Reports | Sim | Sim | |
| MaisCode.Util | Sim | Sim | Restaurado. |
| MaisCode.WinService | Sim | Sim | Pasta `winService` restaurada. |
| MaisCode.InstallService | Sim | Sim | Instalador restaurado. |

**Implicação:** a solução **pode** ser compilável após restaurar pacotes NuGet e ambiente local; isso **não** foi validado na escrita deste arquivo.

### 2.2 Arquivos locais não rastreados (atenção)

Em sessões anteriores apareceram como **não rastreados** (`git status ??`) e possivelmente corrompidos: `MaisCode.Pagamento/LibraryHashtable.cs`, `MaisCode.Pagamento/MPService.cs`, `MaisCode.Pagamento/BibliotecaMP/`. **Não** fazem parte do conjunto mínimo rastreado no Git para Pagamento — revisar ou remover localmente se atrapalharem o build.

## 3. Camadas observadas

- **Domain (`MaisCode.Domain`):** entidades POCO, enums; regras na aplicação.
- **Application (`MaisCode.App`):** `*Service.cs`, DTOs, helpers.
- **Infrastructure (`MaisCode.Data`):** `ApplicationDbContext`, configurações EF, repositório, migrações.
- **Presentation API (`MaisCode.Site`):** Web API + OWIN + autenticação OAuth/cookies; **controllers legíveis**.
- **Presentation loja:** `MaisCode.Ecommerce` — MVC + AngularJS SPA.
- **Integração (`MaisCode.SebraeIntegracao`):** DTOs + **`Metodos`** (clientes HTTP) **legíveis**.
- **Cross:** `MaisCode.Mail`, `MaisCode.Reports`, `MaisCode.Util`, `MaisCode.Identity`.

## 4. Persistência

EF6, `DefaultConnection`, schema padrão `Commerce`, `Integracao.EventoCurso`, hooks em `SaveChanges`. Evidência: `MaisCode.Data/Context/ApplicationDbContext.cs`.

## 5. Identidade e segurança

- Domínio: `ApplicationUser`, `Perfil` com `IdEmpresa`.
- **OWIN:** `Startup.Auth.cs` — `ApplicationDbContext`, `ApplicationUserManager`, cookie auth, two-factor cookies, **OAuthAuthorizationServer** com `TokenEndpointPath = /token`, `AllowInsecureHttp = true` (atenção para hardening em modernização).
- **API:** `DashboardController` ainda pode ter `[Authorize]` comentado (verificar arquivo atual) — revisar política por endpoint.
- Front: Bearer em `siteApp.js`.

## 6. Front-end AngularJS

UI-Router, `siteConfig.js`, `AngularJS/Svc/*.js`, base `$rootScope.urlApi`.

## 7. Testes

- `MaisCode.Pagamento/AuthorizeTest.cs` (rastreado no Git).
- `MaisCode.IntegraTestes` — projeto presente; escopo dos testes: **inventariar** ao rodar solução.

## 8. Git / árvore

O `git status` pode ainda listar **deleções** ou alterações em `MaisCode.Ecommerce` (assets/views) não cobertas pelo restore seletivo. A discovery de **comportamento** prioriza código C# e contratos API; assets faltantes podem afetar apenas a UI estática.
