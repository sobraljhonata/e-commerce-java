# Cursor Operating Rules

## Regras gerais
- Nunca assumir regra de negócio sem evidência no código, testes, migrations, queries, templates, controllers, services, entidades, mensagens de erro ou documentação existente.
- Sempre diferenciar:
  - regra de negócio
  - validação
  - autorização
  - detalhe técnico
  - workaround legado
  - customização Sebrae
- Sempre explicitar nível de confiança:
  - alto
  - médio
  - baixo
- Sempre citar onde a evidência foi encontrada, quando possível.
- Se não houver evidência suficiente, registrar como hipótese e não como fato.
- Não inventar bounded contexts sem justificar com dependências, fluxos e regras.
- Não assumir microsserviços por padrão.
- Não assumir SAGA por padrão.
- Não assumir que `Empresa` é o tenant raiz sem validar no código.
- Não assumir que `Loja` é storefront sem validar no código.
- Não assumir que `Integração` pode ser removida sem validar dependências reais.

## Regras de continuidade
- Sempre ler primeiro:
  - `.ai/context/`
  - `.ai/outputs/01-discovery/`
  - `.ai/outputs/02-product/`
  - `.ai/outputs/03-architecture/`
  - `.ai/outputs/04-migration/`
- Antes de propor uma nova decisão, verificar se ela conflita com decisões já registradas.
- Antes de gerar código, verificar:
  - backlog relacionado
  - bounded context envolvido
  - regra de negócio relacionada
  - ADRs já definidos
  - estratégia de testes já escolhida

## Regras de resposta
- Primeiro explicar a decisão.
- Depois explicar trade-offs.
- Só então sugerir implementação.
- Quando houver dúvida relevante, mostrar a dúvida explicitamente.
- Evitar respostas genéricas.
- Evitar “melhores práticas” sem contexto.