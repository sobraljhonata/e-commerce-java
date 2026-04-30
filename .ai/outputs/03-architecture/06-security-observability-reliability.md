# Segurança, observabilidade, latência e alta disponibilidade

**Base:** `16-nfr-baseline.md`. **Objetivo:** baseline **fechada** para MVP/Wave 1, com espaço para endurecimento.  

---

## 1. Jornadas latency-sensitive (decisão)

| Jornada | Orçamento alvo (p95) | Notas |
|---------|----------------------|--------|
| **Listagem catálogo (API)** | &lt; 300 ms | Cache + índices; CDN para assets. |
| **Detalhe produto** | &lt; 250 ms | |
| **Adicionar ao carrinho** | &lt; 200 ms | Idempotência com chave cliente. |
| **Checkout (submit order)** | &lt; 500 ms **até** confirmação de pedido criado | Exclui tempo do provedor de pagamento externo. |
| **Webhook pagamento** | processamento interno &lt; 2 s p95 | ACK rápido ao provider se necessário + processamento assíncrono. |

**Hipótese:** números ajustados após testes de carga em ambiente representativo. **Fato:** checkout no legado é frágil para escala — nova baseline é **compromisso de engenharia**, não medida atual.  

---

## 2. Disponibilidade (decisão)

| Capability | Alvo MVP |
|------------|----------|
| **Storefront + API** | 99,5% mensal (excluindo janela de manutenção anunciada) |
| **Checkout / Order API** | 99,5% |
| **Payment webhook ingress** | 99,9% (fila + retry) |
| **Admin backoffice** | 99,0% |

**Risco:** dependência de terceiros (PSP, Sebrae) — SLO composto comunicado ao negócio.  

---

## 3. Segurança (decisão)

| Área | Medida |
|------|--------|
| **Transporte** | TLS 1.2+ everywhere; HSTS. |
| **AuthN** | OIDC; tokens curtos; refresh rotativo. |
| **AuthZ** | RBAC por tenant; policy engine simples no serviço (roles + permissions). |
| **API** | Rate limiting (Redis); validação de input; CORS restritivo por ambiente. |
| **Dados** | Criptografia at-rest (RDS/S3); segredos em vault; PII minimizada. |
| **Pagamento** | PCI: **não** armazenar PAN; usar redirect/hosted fields/tokenização do PSP. |
| **Headers** | CSP progressivo; SameSite cookies; CSRF onde cookie session. |
| **Supply chain** | Dependabot/Snyk; imagens base mínimas. |

**Fato:** legado teve sinais de configuração permissiva — **Decisão:** checklist de segurança no go-live de cada ambiente.  

---

## 4. Observabilidade (decisão — nasce junto)

| Pilar | Implementação |
|-------|----------------|
| **Logs** | Estruturados (JSON); `trace_id`, `tenant_id`, `user_id` (hash se LGPD). |
| **Métricas** | RED para APIs; métricas de negócio (pedidos/hora, falha pagamento). |
| **Traces** | OpenTelemetry; span por use case e por chamada a BC11/PSP. |
| **Dashboards** | Por jornada (checkout, integração). |
| **Alertas** | SLO burn rate básico; alerta de taxa de 5xx e latência p95. |

**Recomendação:** “business audit log” imutável para eventos sensíveis (mudança de preço, role, config tenant).  

---

## 5. Resiliência

| Padrão | Onde |
|--------|------|
| **Timeout + retry idempotente** | Chamadas HTTP (PSP, Sebrae). |
| **Circuit breaker** | Integration Hub quando integração degradar. |
| **Bulkhead** | Pool separado para webhooks vs APIs síncronas (se necessário). |
| **Graceful degradation** | Catálogo disponível se integração de disponibilidade falhar — **decisão de produto:** exibir estado “indisponível” consistente, não erro 500. |

---

## 6. Deploy e operação

| Decisão | Detalhe |
|---------|---------|
| **Estratégia** | Rolling ou blue/green no orquestrador escolhido. |
| **Migrations** | Compatíveis backward uma versão (expand/contract quando crítico). |
| **Feature flags** | Para integrações e tipos de produto experimentais. |
| **DR** | Backup RDS PITR; RTO/RPO **definidos** com negócio na Wave 1 (placeholder MVP: RPO ≤ 15 min). |

---

## 7. Custo e complexidade (alertas)

| Alerta | Gatilho |
|--------|---------|
| **Custo infra** | Budget AWS por ambiente; alarme 80% do mês. |
| **Complexidade** | Proposta de novo serviço ou Kafka exige checklist NFR + custo. |

---

## Resumo Fato / Decisão / Risco

| Tipo | Conteúdo |
|------|----------|
| **Fato** | Observabilidade e segregação foram gaps no legado. |
| **Decisão** | Baseline acima é mínimo do MVP; evolução para RLS e SLO mais rígidos é planejada. |
| **Risco** | Subestimar webhooks de pagamento — mitigar com idempotência e DLQ. |
