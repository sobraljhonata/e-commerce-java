# MVP da plataforma

**Definição:** primeiro incremento **greenfield** que prova **tenant real**, **compra segura** e **base de evolução** — alinhado à auditoria (paridade mínima de capacidades transacionais, sem copiar legado).  

---

## Objetivo do MVP (decisão)

Demonstrar que um **novo tenant** pode:

1. Ser provisionado com config e tema básicos.  
2. Publicar **catálogo** com pelo menos **duas famílias de produto** (ex.: conteúdo digital + curso/evento como **placeholders** de regra).  
3. Permitir comprador anônimo ou autenticado adicionar ao **carrinho** e concluir **pedido** com **pagamento sandbox** (cartão e/ou PIX via PSP de teste).  
4. Receber **confirmação** e estado de **pós-compra mínimo** (ex.: “pedido pago” + e-mail).  
5. Operar com **isolamento de tenant** verificável em testes automatizados.  

**Fora do MVP1:** integrações Sebrae reais, consultoria integrada, leads, microsserviços, Kafka, MCP/RAG em produção.  

---

## Capabilities incluídas (mapeamento BC)

| BC | Escopo MVP1 |
|----|-------------|
| BC01 Tenant & Config | CRUD mínimo tenant, tema, parâmetros loja. |
| BC02 Identity | OIDC; usuário admin loja; comprador opcional MVP (guest checkout permitido **decisão:** sim, com e-mail para recibo). |
| BC03 Catalog | CRUD produto por tipo; mídia via S3; sem integração externa de disponibilidade. |
| BC04 Pricing & Promotions | Preço de lista; cupom **opcional** — pode ficar MVP1.1. |
| BC05 Cart | Sacola com expiração. |
| BC06 Order | Estados até paid/failed; auditoria. |
| BC07 Payment | Um PSP sandbox; webhooks; idempotência. |
| BC08 Customer | Cadastro mínimo no checkout. |
| BC09 Fulfillment | **Stub:** marcar item “entregue”/acesso liberado sem integração real Sebrae. |
| BC10 Notifications | E-mail transacional (provedor SMTP/SendGrid). |
| BC11 Integration | **Mock/stub** apenas; contrato de porta definido. |
| BC12 Content | Página institucional mínima ou estática.  

---

## Critérios de aceite (objetivos mensuráveis)

1. **E2E automatizado:** fluxo feliz checkout em &lt; orçamento de latência definido em `06`.  
2. **Teste de segregação:** dois tenants, dados não cruzam (CI).  
3. **Observabilidade:** trace end-to-end do checkout visível no backend de APM.  
4. **Segurança:** scan de dependências sem críticos abertos; pen-test leve interno checklist.  

---

## Hipóteses de produto a validar pós-MVP1

- Compradores preferem cadastro tardio vs obrigatório.  
- Duas famílias de produto cobrem demo comercial suficiente.  

---

## Riscos do MVP

| Risco | Mitigação |
|-------|-----------|
| Escopo “Sebrae já” | Manter adapter mock; não bloquear MVP |
| Pagamento instável | PSP maduro + retries + reconciliação |

---

## Relação com backlog Fase 2

**Fato:** backlog Fase 2 lista paridade ampla — **Decisão:** MVP1 = subconjunto **plataforma-first** acima; itens LEG/Sebrae entram em **MVP2+** com BC11 real.  
