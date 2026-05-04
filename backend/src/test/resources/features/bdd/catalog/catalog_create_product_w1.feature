# language: pt
Funcionalidade: Cadastro de produto (Catalog W1 — incremento 1)
  Para vender na minha loja
  Como operador autenticado
  Quero cadastrar um produto no meu tenant

  Cenário: Cadastro com JWT associa o produto ao tenant do token
    Dado que o operador está autenticado como admin de plataforma
    Quando cadastra um produto com nome "Camiseta" e preço 49.90
    Então a API de produtos responde 201
    E o tenant do produto é o do usuário seed

  Cenário: Consulta por id retorna o produto no tenant do token
    Dado que o operador está autenticado como admin de plataforma
    E que um produto foi cadastrado com nome "Mug" e preço 15.00
    Quando consulta esse produto pelo id do cadastro
    Então a resposta da consulta de produto é 200
    E o nome do produto retornado é "Mug"

  Cenário: Listagem retorna apenas produtos do tenant do token
    Dado que o operador está autenticado como admin de plataforma
    E que dois produtos foram cadastrados para listagem
    Quando lista os produtos
    Então a resposta da listagem de produtos é 200
    E a listagem contém 2 itens do tenant seed

  Cenário: Atualização altera dados do produto no tenant do token
    Dado que o operador está autenticado como admin de plataforma
    E que um produto foi cadastrado com nome "Antes" e preço 10.00
    Quando atualiza o produto cadastrado com nome "Depois" preço 20.00 e ativo "false"
    Então a resposta da atualização de produto é 200
    E o nome na resposta da atualização é "Depois"

  Cenário: Produto pode ser criado vinculado a categoria do mesmo tenant
    Dado que o operador está autenticado como admin de plataforma
    E que categoria de apoio ao produto foi criada com nome "Moda"
    Quando cadastra um produto com nome "Camiseta" e preço 49.90 vinculado à última categoria
    Então a API de produtos responde 201
    E o produto retornado tem o mesmo categoryId da última categoria

  Cenário: Listagem de produtos por categoria no tenant
    Dado que o operador está autenticado como admin de plataforma
    E que categoria de apoio ao produto foi criada com nome "FiltroCat"
    Quando cadastra um produto com nome "SóNesta" e preço 3.00 vinculado à última categoria
    Então a API de produtos responde 201
    Quando lista os produtos filtrados pela última categoria
    Então a resposta da listagem de produtos é 200
    E a listagem filtrada contém 1 item com nome "SóNesta"
