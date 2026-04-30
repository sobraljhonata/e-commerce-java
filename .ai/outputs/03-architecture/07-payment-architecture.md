# Estratégia de pagamentos

**Objetivo:** checkout confiável, PCI minimizado, múltiplos provedores (Brasil: cartão, PIX, boleto conforme roadmap), **isolado** no BC07.  
**Fato (discovery):** encadeamento E2E com adquirente legado **indeterminado** — greenfield **não** replica implementação legada.  

---

## Decisão arquitetural

1. **BC Payment** é o **único** módulo que fala com PSPs; Order emite intenção e consome resultado via **portas**.  
2. **Padrão de integração:** **Payment Service Provider (PSP) via API** com redirect/hosted page ou tokenização — **sem** dados de cartão brutos na plataforma no MVP.  
3. **Idempotency-Key** obrigatório em toda criação de cobrança e confirmação.  
4. **Webhooks** assinados/validados (HMAC ou JWKS do PSP); endpoint dedicado e **registrado** em infra WAF.  

---

## Porta hexagonal (contratos internos)

| Porta | Responsabilidade |
|-------|------------------|
| `CreatePaymentIntentPort` | Inicia cobrança a partir de `orderId`, valor, método, tenant. |
| `CaptureOrConfirmPort` | Confirma captura quando aplicável. |
| `RefundPort` | Estorno parcial/total. |
| `PaymentWebhookPort` | Normaliza evento externo → comando de domínio. |

Implementações: `MercadoPagoAdapter`, `StripeAdapter`, `PagarMeAdapter`, etc. — **uma ativa por tenant ou por país** via config BC01.  

---

## Métodos (prioridade MVP Brasil)

| Método | MVP1 | Notas |
|--------|------|--------|
| **Cartão** | Sim (via PSP) | 3DS quando exigido pelo PSP. |
| **PIX** | Sim | QR dinâmico / copy-paste conforme PSP. |
| **Boleto** | Opcional pós-MVP1 | Fluxo assíncrono + webhook. |
| **Link de pagamento** | Opcional | Útil B2B; mesmo BC07. |

**Hipótese:** um PSP único no MVP reduz custo; segundo PSP como fallback na mesma interface após estabilização.  

---

## Estados e consistência com Order

| Decisão | Detalhe |
|---------|---------|
| **Máquina de estados** | `Order`: draft → pending_payment → paid / failed / cancelled; `Payment`: initiated → authorized → captured / failed. |
| **Consistência** | Transação local: persistir `Payment` + atualizar `Order` na mesma unidade onde possível; quando PSP for apenas eventual, **saga local** (compensação: marcar order failed, liberar estoque digital). |
| **Double payment** | Constraint única por `orderId` + estado terminal. |

**Fato:** legado misturava regras financeiras — **Decisão:** regras de comissão/conciliação **fora** do caminho síncrono do checkout no MVP (export ou job).  

---

## Conformidade e fraude

- **PCI DSS:** escopo reduzido (SAQ A ou equivalente quando aplicável) — validar com assessor ao escolher PSP.  
- **Antifraude:** hook opcional no adapter (ClearSale etc.) **após** MVP1.  
- **LGPD:** finalidade e base legal no fluxo de checkout (BC08 + front).  

---

## Riscos

| Risco | Mitigação |
|-------|-----------|
| Webhook duplicado ou tardio | Idempotência + reconciliação job diário |
| Divergência valor order vs PSP | Validação estrita na criação da intenção |
| Multi-moeda | **Hipótese:** BRL only MVP; modelo permite `currency` no agregado |

---

## Recomendações

1. **Sandbox PSP** desde o primeiro deploy de stage.  
2. **Ledger** simples (tabela append-only) para auditoria financeira — opcional MVP, **recomendado** antes de go-live comercial.  
