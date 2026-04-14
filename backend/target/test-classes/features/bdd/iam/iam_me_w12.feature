# language: pt
Funcionalidade: Autoconhecimento do operador (W1.2 — incremento 3 / tenant no token — incremento 4)
  Para confirmar minha identidade e tenant após o login
  Como operador da plataforma
  Quero consultar quem sou em GET /api/auth/me

  Cenário: perfil após login retorna email, tenant e papéis
    Dado que o operador obteve um token válido
    Quando o operador solicita o perfil autenticado com bearer token
    Então a API de perfil responde 200
    E o corpo contém email, tenant e roles do operador
