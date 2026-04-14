# language: pt
Funcionalidade: Login administrativo (W1.2 — incremento 1)
  Para obter um token de acesso
  Como operador da plataforma
  Quero autenticar com email e senha

  Cenário: Login com sucesso
    Dado que existe um usuário administrativo válido
    Quando o operador envia credenciais corretas
    Então a API de login responde 200
    E o corpo contém um token de acesso

  Cenário: Login inválido
    Dado que existe um usuário administrativo válido
    Quando o operador envia senha incorreta
    Então a API de login responde 401
