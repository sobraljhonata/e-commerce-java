# Arquitetura hexagonal do backend

**Escopo:** backend Spring Boot modular alinhado a `01-bounded-contexts-oficiais.md`.  
**Objetivo:** testabilidade, substituição de adapters (DB, pagamento, Sebrae) sem tocar regras de negócio.  

---

## Decisão

Cada **bounded context** (BC) é um **módulo Maven/Gradle** (ou pacote raiz inequívoco) organizado assim:

```
bc.<name>/
  domain/           # entidades, VOs, domain services, domain events
  application/      # use cases, ports (inbound/outbound interfaces)
  adapter/
    in/web/         # REST controllers → inbound ports
    in/messaging/   # futuro: consumers
    out/persistence/# JPA/MyBatis → outbound ports
    out/integration/# clients HTTP → outbound ports
```

- **Inbound ports:** interfaces que o domínio expõe para orquestração (ex.: `PlaceOrderUseCase`).  
- **Outbound ports:** interfaces que o domínio precisa (ex.: `OrderRepository`, `PaymentGateway`, `SebraeAvailabilityPort`).  
- **Adapters:** implementações trocáveis.  

---

## Fluxo típico (exemplo Order → Payment)

1. **Controller** chama **inbound port** `CheckoutPort.submitOrder(command)`.  
2. **Application service** valida tenant, carrega agregados via **outbound** `OrderRepository`.  
3. Emite **domain events** in-process (ex.: `OrderPlaced`).  
4. **Payment adapter** implementa `PaymentIntentPort` chamado pela aplicação **após** persistência em mesma transação ou **saga local** (ver `08`).  
5. **Webhook** do provedor entra por `adapter.in.web` dedicado ao BC07, atualiza estado via port.  

---

## Persistência

| Decisão | Detalhe |
|---------|---------|
| **ORM** | JPA/Hibernate ou jOOQ (escolha do time; **decisão operacional** na Wave 1). |
| **Transação** | `@Transactional` na camada de aplicação do BC; **não** em controllers. |
| **Schema** | Preferência por **schema PostgreSQL por BC** (`catalog`, `orders`, `payment`) na mesma database no MVP — facilita extração futura. |
| **Migrações** | Versionadas; nunca DDL manual em produção. |

---

## Domain events

| Tipo | Uso |
|------|-----|
| **In-process** | Desacoplar handlers dentro do monólito (auditoria, notificação assíncrona via outbox). |
| **Publicação externa** | Somente via **transactional outbox** + worker (sem Kafka na Wave 1, pode ser polling ou fila SQS). |

---

## Testes (alinhamento agente 06)

| Camada | Objetivo |
|--------|----------|
| **Unitário** | Domínio puro, sem Spring. |
| **Integração** | Adapters com Testcontainers (Postgres, Redis). |
| **Contrato** | APIs públicas e clientes do Integration Hub (Pact ou equivalente) quando integração for crítica. |
| **E2E** | Jornadas: login → carrinho → checkout sandbox. |

**Recomendação:** critério mínimo antes de extrair qualquer BC para serviço separado: cobertura de contrato + observabilidade de fronteira + latência budget respeitado em testes de carga.  

---

## Anti-padrões proibidos

- Controller chamando repositório direto sem use case.  
- Entidade JPA vazando para API pública.  
- Client HTTP de Sebrae fora de `bc.integration`.  
- `tenantId` opcional em queries.  

---

## Fato / Decisão / Risco

| Tipo | Conteúdo |
|------|----------|
| **Fato** | Legado acoplou regras a controllers e serviços gigantes. |
| **Decisão** | Hexagonal **obrigatória** por BC; revisão de PR bloqueia violações de camada. |
| **Risco** | “Anemic domain” — mitigar com revisão de domínio e event storming leve por BC crítico. |
