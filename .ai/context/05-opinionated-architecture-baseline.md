# Opinionated Architecture Baseline

## Posição inicial
Para este contexto, a hipótese arquitetural inicial mais saudável é:

### Fase 1
- backend modularizado
- monólito modular
- fronteiras explícitas por bounded context
- eventos internos de domínio
- multi-tenant desenhado como capacidade transversal
- catálogo, preço, pedido e tenant como eixos centrais
- front-end moderno com base para design system

### Fase 2
- extração de integrações e componentes de alto acoplamento
- adoção seletiva de mensageria
- outbox pattern nos fluxos mais críticos
- BFF/admin se fizer sentido

### Fase 3
- extração de serviços apenas quando houver:
  - pressão de escala diferente
  - autonomia de time
  - domínio claramente isolado
  - necessidade operacional real

## Tese sobre SAGA
SAGA não deve ser introduzida apenas porque a arquitetura ficou distribuída.
Ela deve existir quando houver processo transacional distribuído relevante, por exemplo:
- pedido
- pagamento
- reserva/estoque
- notificação
- faturamento
e quando compensações forem parte real do processo.

## Tese sobre multi-tenant
Multi-tenant deve ser modelado em:
- autenticação/autorização
- configuração
- branding
- catálogo/políticas
- observabilidade
- isolamento de dados
não apenas em FK de Empresa espalhada.

## Tese sobre front-end
A nova camada de front deve nascer já com:
- design tokens
- componentização orientada a domínio e UI
- acessibilidade
- contratos estáveis com backend
- estratégia de evolução para múltiplas lojas/tenants
