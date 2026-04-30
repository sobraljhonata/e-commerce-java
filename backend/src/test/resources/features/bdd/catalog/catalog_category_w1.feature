# language: pt
Funcionalidade: Cadastro de categoria (Catalog W1 — incremento 5)
  Para organizar o catálogo no meu tenant
  Como operador autenticado
  Quero cadastrar e consultar categorias

  Cenário: Cadastro com JWT associa a categoria ao tenant do token
    Dado que o operador de categorias está autenticado como admin de plataforma
    Quando cadastra uma categoria com nome "Moda"
    Então a API de categorias responde 201
    E o tenant da categoria é o do usuário seed

  Cenário: Consulta por id retorna a categoria no tenant do token
    Dado que o operador de categorias está autenticado como admin de plataforma
    E que uma categoria foi cadastrada com nome "Esportes"
    Quando consulta essa categoria pelo id do cadastro
    Então a resposta da consulta de categoria é 200
    E o nome da categoria retornada é "Esportes"
