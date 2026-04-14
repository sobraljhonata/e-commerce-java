# Rewrite Slicing Rules

## Regra principal
A reescrita deve ser planejada por capability de negócio.

## Exemplo de fatias válidas
- tenant management
- autenticação e autorização
- catálogo
- pricing
- storefront
- cliente
- pedido
- pagamento
- vendas/relatórios
- integração legado

## Exemplo de fatias ruins
- reescrever todos os repositories
- migrar todos os controllers
- refatorar toda a camada de services
- modernizar só o front sem contrato de domínio claro

## Regra adicional
Cada fatia deve responder:
- qual valor de negócio entrega;
- qual bounded context toca;
- quais regras cobre;
- como será testada;
- se exige UI;
- se exige integração;
- se exige eventos;
- se exige migração de dados;
- se exige convivência com legado.