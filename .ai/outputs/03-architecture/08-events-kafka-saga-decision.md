# Decisão: eventos, Kafka e SAGA

**Regra do prompt:** não assumir microsserviços, Kafka ou SAGA como obrigatórios. **Objetivo:** fechamento com critérios de evolução.  

---

## Decisão fechada — Wave 1 / MVP

| Tema | Decisão |
|------|---------|
| **Estilo de deploy** | Monólito modular (um runtime). |
| **Comunicação entre BCs** | Chamadas in-process (use cases) + **domain events in-process**. |
| **Publicação assíncrona** | **Transactional outbox** na mesma base PostgreSQL; consumer **in-process** ou worker leve (polling/SQS). |
| **Apache Kafka** | **Não** adotar na Wave 1. |
| **SAGA distribuída** | **Não** no MVP. |
| **SAGA / orquestração local** | **Sim**, apenas **dentro** do monólito para fluxos Order ↔ Payment ↔ Fulfillment quando compensação for necessária. |

---

## Uso de eventos (níveis)

| Nível | Propósito | Tecnologia |
|-------|-----------|------------|
| **Domain event** | Desacoplar handlers no mesmo processo | POJO + dispatcher Spring |
| **Integration event** | Notificar BC externo ou sistema terceiro | Outbox → entrega assíncrona |
| **Analytics event** | BI/produto | Stream opcional pós-MVP ou export batch |

**Decisão:** todo evento que atravessa limite de consistência **persistido** passa pelo **outbox** na mesma transação que altera o agregado.  

---

## Quando reavaliar Kafka (critérios objetivos)

Todas necessárias, não apenas uma:

1. **Throughput** sustentado que exceda custo/benefício de SQS/polling (número a medir em produção).  
2. **Múltiplos consumidores** heterogêneos com necessidade de **replay** e retenção longa.  
3. **Equipe** com operação madura (on-call, runbooks, observabilidade de lag).  
4. **Contratos** versionados e testados (Pact) em produção por ≥ 1 release.  

**Hipótese:** para &lt; 500 pedidos/min, Kafka é **overhead** operacional.  

---

## Quando SAGA distribuída faria sentido

Somente se **dois ou mais runtimes** com transações independentes precisarem de compensação coordenada **e** o custo de inconsistência eventual for aceito pelo negócio.

**Decisão atual:** extrair primeiro **BC Payment** ou **Integration Hub** só após métricas de acoplamento e testes (ver agente 06). Até lá, **saga local** + **outbox** é suficiente.  

---

## Quando **não** usar SAGA (mesmo local)

- Operação ajustável por **edição simples** de estado sem compensação multietapa.  
- Fluxo totalmente síncrono com mesmo agregado e mesma transação DB.  

---

## Fato / Decisão / Risco / Recomendação

| Tipo | Conteúdo |
|------|----------|
| **Fato** | Legado não oferece modelo de eventos confiável como spec. |
| **Decisão** | Sem Kafka/SAGA distribuída na Wave 1; outbox + eventos in-process. |
| **Risco** | Subestimar volume de webhooks — mitigar com fila e back-pressure mesmo sem Kafka. |
| **Recomendação** | Revisar esta decisão no marco “primeiro cliente pagante em produção + 30 dias de métricas”. |
