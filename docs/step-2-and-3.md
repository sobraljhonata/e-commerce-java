# Passo 2 e 3 — Execução prática

## Passo 2 — configurar proteção arquitetural e CI

### Objetivo
Transformar as decisões da Phase 3 em regras executáveis.

### Itens mínimos
1. ArchUnit no backend
2. Testes de segregação multi-tenant
3. Pipeline CI com gates obrigatórios
4. Cobertura mínima
5. OpenAPI gate
6. Regra de dependência do core

### Gates recomendados no CI
- build
- unit tests
- architecture tests
- tenant isolation tests
- api contract generation/validation
- coverage gate

### Ordem de implantação
1. adicionar dependências
2. criar testes de arquitetura
3. criar primeiro teste de segregação multi-tenant
4. ligar tudo no CI
5. só então começar novos BCs

## Passo 3 — começar a Wave 1

### Ordem recomendada
1. W1.0 Platform Skeleton
2. W1.1 Tenant Management
3. W1.2 IAM baseline
4. W1.3 Tenant admin UI shell
5. W1.4 Catalog mínimo

### Critérios de pronto da W1.0
- estrutura hexagonal criada
- ArchUnit verde
- CI verde
- tenant context definido
- ADR enforcement mapeado
- prompt operacional da Phase 4 validado

### Critérios de pronto da W1.1
- tenant criado via API
- leitura isolada por tenant
- testes de segregação passando
- cenários BDD mínimos cobrindo criação e leitura
- observabilidade básica ligada

## Testes obrigatórios já na Wave 1
- arquitetura hexagonal
- segregação tenant A vs tenant B
- contrato da API de Tenant
- validação de entrada
- idempotência básica onde aplicável

## OpenAPI gate
Sempre publicar o spec do serviço e falhar o CI se a geração quebrar.

## Cobertura
Sugestão inicial:
- unit: >= 80% no domínio/aplicação
- architecture tests: obrigatório
- integration: fluxos críticos da wave
