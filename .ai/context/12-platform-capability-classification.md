# Platform Capability Classification

## Objetivo
Ajudar a classificar corretamente cada capability encontrada no legado e no produto.

## Toda capability deve ser classificada em um destes grupos

### 1. Core de plataforma
Capacidades necessárias para que a solução funcione como plataforma multi-tenant e multi-cliente.

Exemplos:
- tenant management
- identity and access
- configuração por tenant
- branding/configuração de loja
- observabilidade transversal
- segurança transversal

### 2. Core transacional
Capacidades centrais da operação comercial do e-commerce.

Exemplos:
- catálogo
- produto
- categoria
- preço
- promoção
- cliente
- pedido
- checkout
- pagamento

### 3. Supporting capability
Capacidades de suporte relevantes, mas não o núcleo da plataforma.

Exemplos:
- locale
- social
- CMS simples
- notificações
- relatórios operacionais
- gestão administrativa complementar

### 4. Commodity
Capacidades necessárias, porém padronizadas e sem grande diferencial competitivo.

Exemplos:
- autenticação básica
- gestão simples de usuários
- upload de mídia
- logging
- auditoria simples

### 5. Legado / acoplamento específico
Capacidades, fluxos, integrações ou comportamentos fortemente ligados ao Sebrae ou a uma implementação histórica específica.

Exemplos:
- integrações específicas
- nomenclaturas específicas
- regras hardcoded por cliente
- permissões específicas do cliente

### 6. Candidata à descontinuação
Capacidades ou comportamentos que parecem pouco úteis, redundantes, frágeis ou fora da estratégia da nova plataforma.

## Regras
- Nunca classificar sem justificar.
- Sempre explicar:
  - por que a capability está nessa categoria;
  - se ela deve ser mantida, evoluída, isolada ou removida;
  - se ela deve entrar no MVP.