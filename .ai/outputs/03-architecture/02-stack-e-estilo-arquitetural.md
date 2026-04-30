# Stack alvo e estilo arquitetural

**Objetivo:** fechar stack e estilo para implementação greenfield, com impacto explícito em NFRs (`16-nfr-baseline.md`).  

---

## Decisão fechada — stack alvo (MVP → escala inicial)

| Camada | Escolha | Notas |
|--------|---------|--------|
| **Runtime API** | **Java 21** + **Spring Boot 3.x** | Monólito modular; um deployable no MVP. |
| **Web / storefront + backoffice** | **Angular** (21 quando estável no pipeline; LTS próxima aceitável) | SPA; SSR avaliado pós-MVP se SEO crítico. |
| **Dados transacionais** | **PostgreSQL** (managed, ex.: RDS/Aurora) | ACID para pedido/pagamento; migrations com Flyway/Liquibase. |
| **Cache / rate limit / sessão** | **Redis** (managed) | Carrinho quente, idempotência curta, feature flags cacheáveis. |
| **Mensageria** | **Não** Kafka na Wave 1 | Ver `08-events-kafka-saga-decision.md`. |
| **Infra** | **AWS** como padrão | EKS ou ECS Fargate + RDS + ElastiCache + S3 + CloudFront; detalhes na Wave 1. |
| **IAM** | **OIDC** (Keycloak self-hosted ou Cognito + IdP corporativo) | Tokens curtos; RBAC por tenant no serviço. |
| **Observabilidade** | **OpenTelemetry** → collector → backend (ex.: Grafana stack ou vendor) | Métricas, traces, logs correlacionados. |
| **IaC / CI** | Terraform ou CDK + pipeline Git (actions ou equivalente) | Ambientes dev/stage/prod desde cedo. |

---

## Alternativa documentada (não escolhida como padrão)

| Alternativa | Quando faz sentido |
|-------------|-------------------|
| **.NET 8 + ASP.NET Core** | Time exclusivamente .NET, ou política corporativa mandatória. |
| **Implicação** | Mesma **arquitetura hexagonal** e **bounded contexts**; troca apenas runtime e ecossistema. |

**Fato:** legado é .NET — **Decisão:** não reutilizar código; stack nova prioriza ecossistema maduro para modular monolith, observabilidade e hiring, alinhado ao agente Platform Architect (Spring/Angular/AWS). **Hipótese:** curva de aprendizado Java/Spring compensada pela clareza modular e ferramentas de mercado. **Risco:** gap de skills — mitigar com padrões internos e pairing na Wave 1.

---

## Estilo arquitetural (decisão)

| Princípio | Aplicação |
|-----------|-----------|
| **Modular monolith** | Um repositório (ou mono-repo com módulos Maven/Gradle) com **fronteiras duras** por pacote `bc.<name>`. |
| **Hexagonal** | Domínio no centro; adapters HTTP, persistence, integrações na borda. |
| **API-first** | OpenAPI gerada ou fonte da verdade; contratos versionados. |
| **Design for extraction** | Módulos com DB **logical separation** (schemas PostgreSQL por BC quando possível; mesma instância no MVP). |
| **Eventos** | Domain events **in-process** + **transactional outbox** para side effects e futura publicação. |

---

## Impacto em NFRs

| NFR | Como a stack suporta |
|-----|----------------------|
| **Latência** | Redis para leituras quentes; queries indexadas; sem hop distribuído desnecessário no MVP. |
| **Disponibilidade** | Multi-AZ RDS/Redis; health checks; deploy rolling. |
| **Resiliência** | Resilience4j (circuit breaker, retry idempotente) nos adapters BC11/BC07. |
| **Segurança** | Spring Security + OIDC; secrets em Parameter Store/Secrets Manager. |
| **Operabilidade** | OTel desde o primeiro endpoint; dashboards por jornada. |
| **Custo** | Monólito reduz superfície operacional vs microsserviços cedo. |

---

## O que não é decisão neste documento

- Fornecedor exato de IdP (fechado na Wave 1 com segurança).  
- Vendor APM específico (desde que OTLP-compatible).  

---

## Recomendações imediatas

1. **Template** do monólito com um BC piloto (ex.: Catalog) já em hexágono completo — evita “big ball” depois.  
2. **Lint arquitetural** (ArchUnit ou equivalente) impedindo dependências proibidas entre BCs.  
