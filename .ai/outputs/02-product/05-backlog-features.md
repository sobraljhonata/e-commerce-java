# Backlog — Features — Fase 2

Cada feature é derivada de capacidades/casos de uso da Fase 1.  
**Tags:** `[PARC]` paridade | `[MOD]` modernização | `[SEB]` Sebrae | `[MT]` multi-tenant | `[DEB]` débito legado.

---

## E-TEN — Tenant e empresa

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-TEN-01 | Cadastro de empresa | Manter dados cadastrais da empresa usadora da plataforma | PARC, MT |
| F-TEN-02 | Parâmetros por empresa | Separar configurações que hoje usam `ParametroSite` e afins por `IdEmpresa` | PARC, MT |
| F-TEN-03 | Política explícita de tenant | Definir como o contexto de empresa é escolhido em cada jornada (URL, login, convite) | MT, MOD |
| F-TEN-04 | Remover “primeira empresa” como padrão | Substituir `GetAll().First()` por contexto explícito | DEB, MT |

## E-IAM — Identidade e acesso

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-IAM-01 | Login de usuário | Autenticação para área logada da loja | PARC |
| F-IAM-02 | Login social | Fluxo citado no front (`loginsocial`) | PARC |
| F-IAM-03 | Perfis e autorização | Perfis com menus e flags admin (domínio Fase 1) | PARC, MT |
| F-IAM-04 | Política de autorização por operação | Garantir que cada operação sensível exija permissão explícita | MOD, MT |
| F-IAM-05 | Sessão segura e revogação | Requisitos de produto para encerrar sessão e trocar senha | MOD |

## E-STORE — Storefront

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-ST-01 | Home com vitrines | Blocos de cursos, eventos, downloads, consultorias | PARC |
| F-ST-02 | Depoimentos | Home e listagem completa | PARC |
| F-ST-03 | Proximidade por estado | Listagem por UF/região | PARC |
| F-ST-04 | Navegação e rotas principais | Equivalente aos estados UI-Router da Fase 1 | PARC, MOD |

## E-CAT — Catálogo e produto

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-CAT-01 | Tipos de produto | Curso, evento, download, consultoria, pacote | PARC |
| F-CAT-02 | Publicação e status | RB-001 | PARC |
| F-CAT-03 | Listagem e filtro cursos/eventos | Inclui regras de metadados (categoria, cidade, tema) | PARC |
| F-CAT-04 | Disponibilidade de curso | RB-003 + `EventoCurso` | PARC, SEB |
| F-CAT-05 | Detalhe de produto | Ficha rica + mídias via parâmetros de site | PARC |
| F-CAT-06 | Metadados neutros de catálogo | Separar dados de integração de dados de vitrine (visão alvo) | SEB, MOD, MT |
| F-CAT-07 | Sincronização com evento externo | Sync por código de evento (F10) | PARC, SEB |
| F-CAT-08 | Produto consultoria | Persistência via fluxo dedicado (F9, RB-010) | PARC, SEB, MT |

## E-CART — Sacola e pedido

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-CRT-01 | Criar e atualizar pedido | Inclui código amigável RB-004 | PARC |
| F-CRT-02 | Remover item e limpar sacola | RB-005 | PARC |
| F-CRT-03 | Vínculo de participantes | Endpoint de vínculo pessoas-produto-pedido (F4) | PARC |
| F-CRT-04 | Consistência por tenant | Garantir que pedido só referencie produtos da mesma empresa | MT |

## E-PROMO — Promoções

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-PRM-01 | Validação de cupom | RB-006 | PARC |
| F-PRM-02 | Campanhas | Entidades citadas na Fase 1 — escopo funcional a detalhar com time | PARC, MT |

## E-CHK — Checkout e venda

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-CHK-01 | Processo de pagamento (loja) | Passo `pagamento` antes da venda (F5) | PARC |
| F-CHK-02 | Efetivar venda | RB-007 | PARC |
| F-CHK-03 | Termos de aceite | Listagem/consentimento (F5) | PARC |
| F-CHK-04 | Inscrição paga integrada | RB-009 | PARC, SEB |
| F-CHK-05 | Notificação pós-compra | RB-008 — hoje com copy Sebrae | PARC, SEB, MOD |

## E-PAY — Pagamento (negócio)

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-PAY-01 | Registrar resultado de pagamento | Estados de negócio “autorizado/capturado/falho” sem nomear adquirente | PARC, MOD |
| F-PAY-02 | Parametrizar formas de pagamento | Substituir IDs fixos da integração Sebrae (RB-009) | SEB, MT, MOD |
| F-PAY-03 | Múltiplos meios modernos | PIX, link, checkout transparente — **objetivo** em `.ai/context/02-modernization-goals.md` | MOD |
| F-PAY-04 | Clarificar escopo MaisCode.Pagamento | Discovery — alinhar com time o que já existe | PARC |

## E-CUST — Cliente

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-CUS-01 | Cadastro PF | Inclui fallback de empresa (RB-011) a corrigir | PARC, DEB, MT |
| F-CUS-02 | Cadastro PJ | Idem | PARC, DEB, MT |
| F-CUS-03 | Endereços e contatos | Serviços citados na Fase 1 | PARC |
| F-CUS-04 | Consulta cadastral integrada | CPF/CNPJ via integração (F6) | PARC, SEB |

## E-POST — Pós-compra

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-PST-01 | Histórico de vendas | Por cliente | PARC |
| F-PST-02 | Entrega de downloads | Por tipo download (RB-002) | PARC |
| F-PST-03 | Lista de desejos | CRUD citado no front | PARC |

## E-CMS — Conteúdo

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-CMS-01 | Páginas HTML dinâmicas | Por nome/id | PARC |
| F-CMS-02 | FAQ | Listagem | PARC |
| F-CMS-03 | Fale Conosco | Registro de contato | PARC |
| F-CMS-04 | Branding configurável | Substituir textos/URLs fixos Sebrae onde aplicável | MOD, SEB, MT |

## E-LEAD — Lead

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-LED-01 | Captura de lead | POST lead (F11) | PARC |
| F-LED-02 | Roteamento de notificação | Corrigir destino sempre “primeira empresa” (RB-012) | DEB, MT |

## E-INT-SEB — Integração Sebrae

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-SEB-01 | Contrato de eventos/cursos | Leitura de disponibilidade desacoplada da vitrine | SEB, MT |
| F-SEB-02 | Contrato financeiro | POST venda ecommerce (payload Fase 1) | SEB |
| F-SEB-03 | Cadastro e reservas | Endpoints do front (discovery servidor) | PARC, SEB |
| F-SEB-04 | CEP / endereço | Provedor configurável vs Sebrae | SEB, MOD, MT |
| F-SEB-05 | Anti-corrupção e limites | Timeouts, idempotência, DLQ **como requisito de produto** | MOD, SEB |

## E-ADM — Admin

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-ADM-01 | Dashboard agregado | Dados operacionais (rota confirmada Fase 1) | PARC |
| F-ADM-02 | Relatórios | `ReportController` — detalhar com time | PARC |
| F-ADM-03 | Menus administrativos | `MenuAdm` | PARC, MT |
| F-ADM-04 | Upload de arquivos | Casos de uso de negócio a validar | PARC |
| F-ADM-05 | Gestão de banners, temas, menus loja | Serviços Loja/* | PARC, MT |

## E-OBS — Confiabilidade

| ID | Feature | Descrição | Tags |
|----|---------|-----------|------|
| F-OBS-01 | Trilha de auditoria de negócio | Quem alterou o quê em entidades críticas | MOD, MT |
| F-OBS-02 | Indicadores de falha de jornada | Checkout, integração, pagamento | MOD |

---

## Contagem por visão (aproximada)

| Visão | Features com tag principal |
|-------|----------------------------|
| Paridade | Maioria das F-*-01…03 |
| Modernização | F-IAM-04/05, F-CMS-04, F-PAY-01/03, F-OBS-*, F-ST-04 |
| Sebrae | F-CAT-04/06/07, F-CHK-04/05, F-CUS-04, F-INT-SEB-* |
| Multi-tenant | F-TEN-*, F-CRT-04, F-CUS-01/02, F-LED-02, F-PRM-02, F-ADM-03/05 |
