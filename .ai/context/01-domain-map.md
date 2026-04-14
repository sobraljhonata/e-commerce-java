# Domain Map — versão opinativa

## Hipótese inicial de bounded contexts
### 1. Identity & Access
- Account
- autenticação
- autorização
- perfis
- acesso administrativo

### 2. Tenant Management
- Empresas
- configuração por tenant
- branding
- políticas por tenant
- segregação de dados

### 3. Storefront Management
- Loja
- social
- locale
- conteúdo/canal
- experiência da loja

### 4. Catalog
- Produtos
- Categorias
- atributos
- disponibilidade
- organização do catálogo

### 5. Pricing & Promotions
- Descontos
- regras promocionais
- preço final
- campanhas

### 6. Customer
- Cliente
- cadastro
- preferências
- histórico

### 7. Order Management
- carrinho, se existir
- Pedido
- ciclo do pedido
- status
- confirmação

### 8. Sales / Commercial Reporting
- Vendas
- consolidação
- indicadores
- operação comercial

### 9. Legacy Integration
- Integração
- acoplamentos Sebrae
- adapters temporários

## Hipóteses que precisam ser confirmadas
- Produto e Pricing podem estar excessivamente misturados hoje.
- Pedido e Venda podem ser o mesmo fluxo em níveis diferentes.
- Loja talvez seja storefront por tenant, e não tenant raiz.
- Locale e Social parecem mais parte da experiência/canal do que do core transacional.
