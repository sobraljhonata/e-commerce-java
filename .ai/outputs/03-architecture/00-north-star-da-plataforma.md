# North Star da plataforma

**Papel:** visão única de produto e arquitetura para a Phase 3 greenfield.  
**Entradas:** discovery (capacidades e riscos do legado como **fatos de negócio**, não como stack), produto Fase 2, auditoria estratégica.  
**Data:** 2026-04-07  

---

## Declaração (North Star)

> **Construir uma plataforma comercial digital moderna, multi-tenant e multi-cliente, onde compradores concluem jornadas de compra (incluindo educação, eventos e conteúdo digital) com baixa latência, alta confiabilidade e isolamento forte entre tenants — com integrações externas (ex.: ecossistema Sebrae) atrás de portas estáveis, e evolução por capabilities sem reaproveitar código legado.**

---

## O que isso significa (decisões implícitas)

| Dimensão | Decisão |
|----------|---------|
| **Produto** | Plataforma reutilizável (SaaS / white-label capaz), não fork por cliente como padrão. |
| **Domínio** | E-commerce vertical educação/evento/download como **família de produto** de primeira classe (`ETipoProduto` no legado ancora o conceito; **modelo novo** não copia o legado). |
| **Confiança** | Tenant context explícito, segurança e observabilidade são capabilities de plataforma desde o MVP. |
| **Inovação** | Tempo para consolidar UX, catálogo inteligente (assistido) e operação — IA em camadas **adjacentes** ao core transacional, não no caminho quente sem justificativa. |
| **Legado** | Apenas fonte de discovery, contratos de integração e validação de comportamento esperado; **zero** herança de código ou limitações não justificadas. |

---

## Classificação (legenda)

- **Fato (discovery):** evidência do sistema atual ou do negócio documentado.  
- **Decisão:** compromisso da Phase 3 para o greenfield.  
- **Hipótese:** acredita-se verdadeiro; exige validação.  
- **Risco:** ameaça a atingir a North Star.  
- **Recomendação:** ação sugerida sem ser ainda decisão fechada de implementação.  

---

## Alinhamento com evidências

| Tipo | Conteúdo |
|------|----------|
| **Fato [discovery]** | Jornada vitrine → checkout → pós-compra existe como capacidade de referência; multi-tenant no legado é **schema multi-empresa** com riscos de segregação. |
| **Decisão** | A nova plataforma **define** tenant como capability de primeira classe; checkout e pagamento são contextos isolados e testáveis. |
| **Hipótese** | O diferencial competitivo virá da combinação **confiabilidade operacional + UX + famílias de produto + integrações plugáveis**, não só de troca de framework. |
| **Risco** | Escopo “copiar legado” disfarçado de modernização — mitigado por ADRs e MVP cortado por capability. |
| **Recomendação** | Medir North Star com SLIs: latência de checkout, taxa de erro de pagamento, incidentes cross-tenant, tempo de onboarding de novo tenant. |

---

## Métricas de sucesso (proposta fechada para acompanhamento)

1. **Confiança transacional:** erro de checkout atribuível à plataforma abaixo de alvo definido em `06-security-observability-reliability.md` (baseline operacional).  
2. **Isolamento:** zero incidente confirmado de vazamento de dados entre tenants em ambientes controlados de teste antes de escala comercial.  
3. **Time-to-tenant:** provisionar tenant “hello world” (catálogo mínimo + checkout sandbox) em tempo alvo acordado com negócio (detalhado na Wave 1).  
4. **Evolução:** capacidade de trocar provedor de pagamento ou adapter de integração sem reescrever núcleo de pedido.

---

## Fora do escopo da North Star (explícito)

- Paridade 1:1 com cada particularidade do legado sem priorização de negócio.  
- Microsserviços ou Kafka como fim em si.  
- IA decidindo autorização, preço final, ou captura de pagamento.
