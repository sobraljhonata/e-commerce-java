# language: pt
Funcionalidade: Proteção de APIs administrativas (W1.2 — incremento 2)
  Para garantir que só operadores autenticados acessem o painel
  Como plataforma
  Quero exigir JWT Bearer em /api/admin/** e manter /api/auth/login público

  Cenário: Acesso a recurso admin sem autenticação
    Dado que o operador não está autenticado
    Quando solicita tenant por slug inexistente sem bearer token
    Então a API admin responde 401

  Cenário: Acesso a recurso admin com token válido
    Dado que o operador obteve um token válido
    Quando solicita tenant por slug inexistente com bearer token
    Então a API admin responde 404
