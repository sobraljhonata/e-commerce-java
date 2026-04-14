# language: pt
@tenant @w11
Funcionalidade: Administração de tenants (W1.1)
  Como operador de plataforma
  Quero gerenciar tenants via API admin
  Para provisionar e controlar lojas de forma consistente

  Cenário: Criar tenant com slug único
    Dado que o catálogo de tenants está vazio
    Quando o operador cria um tenant com slug "loja-norte" e nome de exibição "Loja Norte"
    Então a API responde 201
    E o corpo contém slug "loja-norte"
    E o corpo indica que o tenant está ativo
    E o corpo contém um id de tenant válido

  Cenário: Rejeitar criação quando o slug já existe
    Dado que já existe um tenant com slug "acme" e nome "Acme Ltda"
    Quando o operador tenta criar outro tenant com slug "acme" e nome "Duplicado"
    Então a API responde 409
    E o corpo de erro tem código "DUPLICATE_TENANT_SLUG"
    E o corpo de erro referencia o slug "acme"

  Cenário: Consultar tenant por id
    Dado que existe um tenant com slug "por-id" e nome "Por Id"
    Quando o operador consulta o tenant pelo id retornado na criação
    Então a API responde 200
    E o corpo contém slug "por-id"

  Cenário: Consultar tenant por slug
    Dado que existe um tenant com slug "por-slug" e nome "Por Slug"
    Quando o operador consulta o tenant pelo slug "por-slug"
    Então a API responde 200
    E o corpo contém slug "por-slug"

  Cenário: Consultar tenant por id inexistente
    Dado que o catálogo de tenants está vazio
    Quando o operador consulta um tenant por um id aleatório
    Então a API responde 404
    E o corpo de erro tem código "TENANT_NOT_FOUND"
    E o corpo de erro referencia o tenantId consultado

  Cenário: Desativar tenant
    Dado que existe um tenant ativo com slug "ativa" e nome "Ativa"
    Quando o operador define o status do tenant como inativo
    Então a API responde 200
    E o corpo indica que o tenant está inativo

  Cenário: Reativar tenant
    Dado que existe um tenant inativo com slug "reabre" e nome "Reabre"
    Quando o operador define o status do tenant como ativo
    Então a API responde 200
    E o corpo indica que o tenant está ativo
