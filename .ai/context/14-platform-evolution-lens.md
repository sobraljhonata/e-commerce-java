# Platform Evolution Lens

## Objetivo
Forçar a análise do sistema com mentalidade de evolução para plataforma.

## Toda análise deve responder

### Visão de produto
- isso é capacidade de negócio real?
- isso gera valor para uma loja virtual multi-cliente?

### Visão de plataforma
- isso pode ser configurável por tenant?
- isso suporta múltiplos clientes sem hardcode?

### Visão de modularização
- isso deveria ser um módulo próprio?
- isso deveria ser agrupado com quais outras capacidades?

### Visão de segurança
- isso aumenta risco de exposição de dados?
- isso pode gerar vazamento entre tenants?

### Visão de escalabilidade
- isso pode escalar sem reescrita drástica?
- isso exige desacoplamento futuro?

### Visão de operação
- isso é observável?
- isso é auditável?
- isso é suportável em produção?

## Regra
Sempre separar:
- o que é bom o suficiente para MVP
- o que é necessário para operar bem
- o que é necessário para escalar
- o que é desejável, mas pode esperar