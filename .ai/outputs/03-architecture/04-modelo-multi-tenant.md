# Estratégia de multi-tenant

**Objetivo:** isolamento forte, custo operacional controlado e caminho de evolução para clientes enterprise.  
**Fato (discovery):** legado usa `IdEmpresa` proliferado com risco de comportamento single-tenant (`First()`). **Decisão:** tenant como **primeira classe** em toda requisição e persistência.  

---

## Decisão — modelo para MVP e escala inicial

| Aspecto | Escolha |
|---------|---------|
| **Isolamento de dados** | **Shared database, shared schema**, coluna obrigatória **`tenant_id`** (UUID) em todas as tabelas de negócio. |
| **Resolução de tenant** | Subdomínio (`{slug}.loja.com`) + header opcional `X-Tenant-Id` **apenas** para APIs internas com validação cruzada. |
| **Autorização** | Após autenticação OIDC, **membership** usuário↔tenant em BC02; JWT carrega `tenant_id` ativo **assinado pelo serviço** (não confiar só no cliente). |
| **Row-Level Security (RLS)** | **Recomendado** antes de multi-cliente produção ampla; **Hipótese:** habilitar PostgreSQL RLS na fase imediatamente posterior ao MVP interno. |

---

## Regras obrigatórias

1. **Tenant context:** filtro Hibernate / interceptor ou padrão repository que **injeta** `tenant_id` em todo SELECT/INSERT/UPDATE.  
2. **Proibição:** query sem filtro de tenant exceto em jobs administrativos com role `PLATFORM_ADMIN` e auditoria.  
3. **Caches:** chave sempre prefixada `tenant:{id}:...`.  
4. **Filas / outbox:** payload inclui `tenant_id`; consumer valida consistência.  
5. **Integrações:** BC11 propaga tenant ao chamar Sebrae; mapeamento tenant↔códigos externos em tabela de configuração versionada.  

---

## Hierarquia multi-cliente (organizações)

| Conceito | Definição |
|----------|-----------|
| **Tenant** | Unidade de isolamento (loja/cliente comercial). |
| **Organization (opcional)** | Agrupa vários tenants para holding; **MVP:** pode ser 1:1 com tenant; modelo de dados prevê FK opcional. |

**Decisão:** não implementar hierarquia complexa na Wave 1; apenas **hooks** no modelo (nullable `parent_org_id`) se negócio exigir roadmap.  

---

## Evolução (pós-MVP)

| Estágio | Quando | O que muda |
|---------|--------|------------|
| **T1** | MVP | Coluna + aplicação enforced. |
| **T2** | Primeiros clientes pagantes externos | RLS + testes de segregação automatizados em CI. |
| **T3** | Cliente enterprise / regulatório | Schema dedicado ou database dedicado por tenant (operacionalmente mais caro). |

**Hipótese:** &lt; 5% dos tenants exigirão T3 no primeiro ano — validar com negócio.  

---

## Branding e configuração

- **BC01** armazena tema (tokens referenciados pelo front), logos (S3), textos legais.  
- **Front:** consome **Design System** + **tenant theme** (CSS variables ou JSON de tokens).  

---

## Riscos e mitigação

| Risco | Mitigação |
|-------|-----------|
| Vazamento por query esquecida | ArchUnit + code review + RLS |
| Cache cross-tenant | Prefixo obrigatório + testes |
| Job batch errado | Scope explícito por tenant ou modo global auditado |

---

## Recomendações

1. **Testes de segregação** como gate de release (tag **[Segregação]** da auditoria).  
2. **Simulador de tenant** em ambiente de stage com dados fictícios paralelos.  
