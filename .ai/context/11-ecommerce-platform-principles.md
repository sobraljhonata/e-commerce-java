# E-commerce Platform Principles

## Objetivo
Guiar decisões para transformar o legado em uma plataforma de comércio multi-tenant e multi-cliente.

## Princípios
- Tenant é capacidade de plataforma, não detalhe de banco.
- Produto, preço, pedido e pagamento são eixos críticos do domínio.
- Integrações específicas de cliente devem ser isoladas.
- O sistema deve suportar múltiplos clientes sem hardcodes.
- O sistema deve suportar múltiplas lojas/canais por tenant, se o domínio exigir.
- Configuração por cliente deve ser explícita e auditável.
- Catálogo e políticas comerciais devem poder variar por tenant.
- Segurança, observabilidade e segregação são capacidades transversais.
- O desenho deve permitir grupos de módulos por famílias de produto quando fizer sentido.
- A plataforma deve evoluir por capabilities, não por camadas genéricas.
- O MVP deve priorizar operação segura e fluxo de compra confiável.
