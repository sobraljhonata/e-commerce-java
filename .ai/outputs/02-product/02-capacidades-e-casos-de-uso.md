# Capacidades e casos de uso — Fase 2

**Fontes:** Fase 1 (`02-mapa-de-modulos.md`, `04-fluxos.md`, `03-regras-de-negocio.md`).  
**Convenção de tags (backlog):** `PARC` paridade | `MOD` modernização | `SEB` Sebrae | `MT` multi-tenant.

---

## 1. Mapa de capacidades do produto (legado)

| ID | Capacidade | Descrição resumida | Evidência principal |
|----|------------|--------------------|---------------------|
| C01 | **Vitrine e descoberta** | Home com blocos (próximos eventos, cursos, downloads, consultorias), depoimentos, opcional proximidade por estado | F1, `HomeSvc.js` |
| C02 | **Catálogo de cursos e eventos** | Listar, filtrar (nome, categoria, cidade, datas, ordenação), metadados de filtro | F2, `CursosEventosSvc.js` |
| C03 | **Catálogo de conteúdos** | Rota `/conteudos` (downloads como vitrine) | `siteConfig.js` (Fase 1) |
| C04 | **Ficha de produto** | Detalhe por id, relacionados por categoria/tema, imagens/copy de parâmetros do site | F3, `DetalheSvc.js` |
| C05 | **Gestão de sacola (pedido)** | Criar/atualizar pedido, remover item, esvaziar sacola, vínculo pessoas-produto no pedido | F4, RB-004/005 |
| C06 | **Promoções — cupom** | Validar cupom por código e janela de validade | F4, RB-006 |
| C07 | **Checkout e venda** | Pagamento, efetivar venda, termos de aceite, inscrição paga com integração financeira | F5, RB-007/009 |
| C08 | **Conta e identidade** | Login, login social, cadastro/salvamento de usuário, operações de cliente | F6, `UserSvc.js` |
| C09 | **Cadastro cliente PF/PJ** | Persistência de cliente com endereços e contatos; integração consulta CPF/CNPJ | F6, serviços App (Fase 1) |
| C10 | **Reserva / inscrição (Sebrae)** | Efetivar inscrição, cancelar reserva de evento (endpoints citados no front) | F6 — **regras servidor não fechadas** na Fase 1 para todos os paths |
| C11 | **Pós-compra** | Listar vendas por cliente, downloads adquiridos, lista de desejos | F7 |
| C12 | **Conteúdo institucional** | Páginas HTML dinâmicas, FAQ, Fale Conosco | F8 |
| C13 | **Social — depoimentos** | Listagem home e completa | F1 |
| C14 | **Social — lead** | Captura e notificação por e-mail (para e-mail da empresa usada no fluxo) | F11, RB-012 |
| C15 | **Consultoria (integração)** | Efetivar/buscar consultoria; gravar produto consultoria | F9, RB-010 |
| C16 | **Sincronização catálogo ↔ evento Sebrae** | Sync por código de evento; lógica em `ProdutoService` | F10 |
| C17 | **Configuração da “loja” por empresa** | Banners, menus, temas, parâmetros de site, etc. (serviços + controllers API nomeados) | `02-mapa-de-modulos.md` |
| C18 | **Administração de empresa e cadastros auxiliares** | Empresa, locale (UF/país/cidade), categorias, campanhas, perfis | Controllers API (lista na Fase 1) |
| C19 | **Relatórios / dashboard** | Pelo menos indicadores agregados no dashboard (`DashboardService` / rota confirmada na Fase 1) | `DashboardController` |
| C20 | **Upload de arquivos** | Superfície `UploadFileController` (detalhe funcional não especificado na Fase 1) | Lista de controllers |
| C21 | **Notificações por e-mail** | Envio configurável (Gmail/KingHost citado) com templates incluindo copy Sebrae | RB-008, `VendaService` |
| C22 | **Projeto Pagamento (legado)** | Existem tipos `Acquirer`, `Query`, teste `AuthorizeTest` na árvore — **escopo funcional não fechado** na Fase 1 | `07-lacunas` |

---

## 2. Casos de uso (consolidados)

Formato: **UC-XX** | Ator principal | Capacidades | Tags sugeridas.

### Descoberta e navegação

| UC | Nome | Ator | Cap. | PARC | MOD | SEB | MT |
|----|------|------|------|:----:|:---:|:---:|:--:|
| UC-01 | Explorar home e vitrines | A1 | C01,C13 | ● | ○ | ○ | ○ |
| UC-02 | Filtrar cursos e eventos | A1 | C02 | ● | ○ | ● | ○ |
| UC-03 | Explorar conteúdos (downloads) | A1 | C03 | ● | ○ | ○ | ○ |
| UC-04 | Ver detalhe do produto | A1 | C04 | ● | ○ | ○ | ○ |

### Compra

| UC | Nome | Ator | Cap. | PARC | MOD | SEB | MT |
|----|------|------|------|:----:|:---:|:---:|:--:|
| UC-05 | Montar sacola | A2 | C05 | ● | ○ | ○ | ○ |
| UC-06 | Aplicar cupom | A2 | C06 | ● | ○ | ○ | ○ |
| UC-07 | Concluir compra (checkout) | A2 | C07 | ● | ○ | ● | ○ |
| UC-08 | Aceitar termos quando exigidos | A2 | C07 | ● | ○ | ○ | ○ |

### Conta e cadastro

| UC | Nome | Ator | Cap. | PARC | MOD | SEB | MT |
|----|------|------|------|:----:|:---:|:---:|:--:|
| UC-09 | Autenticar-se (login / social) | A2 | C08 | ● | ● | ○ | ○ |
| UC-10 | Registrar-se e manter cadastro de usuário | A2 | C08 | ● | ● | ○ | ○ |
| UC-11 | Manter cadastro de cliente PF/PJ | A2 | C09 | ● | ○ | ● | ● |
| UC-12 | Consultar situação cadastral via integração (CPF/CNPJ) | A2/A4 | C09 | ● | ○ | ● | ○ |
| UC-13 | Efetivar inscrição / cancelar reserva de evento | A2 | C10 | ● | ○ | ● | ○ |

*Nota UC-13:* endpoints citados no front; validação completa no servidor **não** consolidada na Fase 1 — manter como **discovery**.

### Pós-compra e engajamento

| UC | Nome | Ator | Cap. | PARC | MOD | SEB | MT |
|----|------|------|------|:----:|:---:|:---:|:--:|
| UC-14 | Ver histórico de compras e downloads | A2 | C11 | ● | ○ | ○ | ○ |
| UC-15 | Gerenciar lista de desejos | A2 | C11 | ● | ○ | ○ | ○ |
| UC-16 | Enviar lead | A1 | C14 | ● | ○ | ○ | ● |

### Conteúdo e institucional

| UC | Nome | Ator | Cap. | PARC | MOD | SEB | MT |
|----|------|------|------|:----:|:---:|:---:|:--:|
| UC-17 | Ler FAQ e páginas institucionais | A1 | C12 | ● | ○ | ○ | ○ |
| UC-18 | Registrar contato (Fale Conosco) | A1 | C12 | ● | ○ | ○ | ○ |

### Integração consultoria e catálogo

| UC | Nome | Ator | Cap. | PARC | MOD | SEB | MT |
|----|------|------|------|:----:|:---:|:---:|:--:|
| UC-19 | Concluir fluxo de consultoria integrada | A5/A2 | C15 | ● | ○ | ● | ● |
| UC-20 | Sincronizar produto com evento Sebrae | A3 | C16 | ● | ○ | ● | ○ |

### Operação (back-office)

| UC | Nome | Ator | Cap. | PARC | MOD | SEB | MT |
|----|------|------|------|:----:|:---:|:---:|:--:|
| UC-21 | Configurar aparência e conteúdo da loja | A3 | C17 | ● | ● | ○ | ● |
| UC-22 | Manter cadastros (empresa, catálogo auxiliar, campanhas) | A3 | C18 | ● | ○ | ○ | ● |
| UC-23 | Visualizar dashboard operacional | A3 | C19 | ● | ● | ○ | ● |
| UC-24 | Gerar/obter relatórios | A3 | C19 | ● | ● | ○ | ● |
| UC-25 | Enviar arquivo (upload) | A3 | C20 | ● | ○ | ○ | ○ |

---

## 3. Casos de uso explícitos “fora da loja” (discovery)

| UC-D1 | **Serviço Windows de integração** | Hipótese H4 — confirmar com time e repositório. |
| UC-D2 | **Pagamento adquirente (MaisCode.Pagamento)** | Código presente; contrato funcional não fechado na Fase 1. |

---

## 4. Débitos legados expressos como comportamento

| ID | Comportamento observado | Impacto | UC afetados |
|----|-------------------------|---------|-------------|
| DL-01 | `IdEmpresa` definido pela **primeira** empresa em vários fluxos | Tenant errado | UC-11, UC-16, UC-19 |
| DL-02 | Disponibilidade de curso acoplada a `EventoCurso` | Catálogo depende de integração | UC-02 |
| DL-03 | Constantes de forma de pagamento na integração financeira | Fragilidade operacional | UC-07 |
| DL-04 | E-mail com marca Sebrae fixa | Multi-marca | UC-07, UC-14 |
