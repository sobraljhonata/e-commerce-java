# Backlog → Bounded Context Mapping

| ID | Capability | Epic | BC | Tipo | MVP | Observação |
|----|------------|------|----|------|-----|-----------|
| C01 | Tenant onboarding | EP-01 | Tenant | Core Plataforma | ✅ | crítico |
| C02 | Auth login | EP-01 | IAM | Core Plataforma | ✅ | OIDC |
| C03 | Criar produto | EP-02 | Catalog | Core Transacional | ✅ | família 1 |
| C04 | Categoria | EP-02 | Catalog | Core Transacional | ✅ | |
| C05 | Preço base | EP-03 | Pricing | Core Transacional | ✅ | |
| C06 | Carrinho | EP-04 | Cart | Core Transacional | ✅ | |
| C07 | Pedido | EP-05 | Order | Core Transacional | ✅ | |
| C08 | Pagamento | EP-06 | Payment | Core Transacional | ⚠️ | PSP sandbox |
| C09 | Email pedido | EP-07 | Notifications | Supporting | ✅ | |
| C10 | Integração Sebrae | EP-08 | Integration Hub | Legacy | ❌ | fora MVP |
| EP-01 | Tenant foundation | Create tenant | Tenant | Sim | Wave 1 |
| EP-02 | Identity | Admin login | IAM | Sim | depende de Tenant |
| EP-03 | Catalog | Create product | Catalog | Sim | depende de Tenant |
| EP-04 | Pricing | Base price | Pricing | Sim | depois de Catalog |