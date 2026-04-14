# Validation and Testing Needs

## Objetivo
Marcar explicitamente o que precisa de validação adicional antes de virar verdade de arquitetura ou backlog.

## Toda descoberta importante deve ser marcada como um destes tipos

### 1. Confirmado por código
Quando a evidência no código é suficiente.

### 2. Confirmado por fluxo funcional
Quando há indícios fortes no front + back + regras + navegação.

### 3. Hipótese forte
Quando há evidência parcial, mas ainda não é confirmação total.

### 4. Requer teste manual no sistema em execução
Usar quando for necessário validar comportamento real em runtime.

Exemplos:
- fluxo de checkout
- manipulação de preço
- regras de desconto
- permissões
- comportamento multi-tenant
- efeitos de integração

### 5. Requer validação com negócio
Usar quando o código mostra um comportamento, mas não fica claro se isso é regra desejada ou workaround histórico.

### 6. Requer teste de segurança
Usar quando houver suspeita de:
- vazamento de dados
- falha de autorização
- tenant leakage
- manipulação de preço/desconto
- inconsistência de sessão/autenticação

## Regra
Sempre marcar explicitamente quais achados precisam de:
- teste manual
- teste de segurança
- validação com negócio
- teste de segregação entre tenants