# Backlog — Tasks — Fase 2

Tasks decompõem histórias em trabalhos **orientados a resultado**, sem escolher stack.  
**Tags:** PARC | MOD | SEB | MT | DEB | DISC (discovery)

---

## Convenções

- **DISC** = entrevista, leitura de código legado, workshop, validação jurídica/operacional.
- Tasks podem ser reutilizadas em várias histórias (referência cruzada).

---

## Discovery e alinhamento (transversal)

| ID | Task | Tags | Liga a |
|----|------|------|--------|
| T-D01 | Inventariar rotas HTTP reais pós-restauração do repositório e cruzar com `04-fluxos.md` | DISC, PARC | Fase 1 lacunas |
| T-D02 | Workshop: definir “tenant” na prática (host, path, login, convite) | DISC, MT | US-021, E-TEN |
| T-D03 | Workshop: prioridade de desacoplamento Sebrae vs paridade total | DISC, SEB | Roadmap |
| T-D04 | Validar atomicidade pedido→venda com time técnico/negócio | DISC, DEB | RB-007 |
| T-D05 | Mapear relatórios existentes (`ReportController` + telas) | DISC, PARC | US-061 |
| T-D06 | Documentar papel de `MaisCode.Pagamento` e adquirentes | DISC, PARC | US-080 |

---

## US-001 — Vitrine (T-ST-01…)

| ID | Task | Tags |
|----|------|------|
| T-ST-01 | Especificar conteúdo mínimo de cada bloco da home vs legado | PARC |
| T-ST-02 | Definir comportamento quando API de listagem retorna vazio | MOD |
| T-ST-03 | Definir regras de ordenação e limite de itens por bloco | PARC |

## US-002 — Busca cursos/eventos (T-SR-01…)

| ID | Task | Tags |
|----|------|------|
| T-SR-01 | Formalizar lista de filtros e combinações suportadas | PARC |
| T-SR-02 | Documentar dependência de `EventoCurso` para “disponível” | SEB, PARC |
| T-SR-03 | Definir mensagens de erro amigáveis para falha de integração | MOD, SEB |

## US-003 — Ficha produto (T-PD-01…)

| ID | Task | Tags |
|----|------|------|
| T-PD-01 | Lista de atributos obrigatórios na ficha por `ETipoProduto` | PARC |
| T-PD-02 | Regras de exibição de mídia e textos legais | PARC, MOD |

## US-004 — Sync evento (T-SY-01…)

| ID | Task | Tags |
|----|------|------|
| T-SY-01 | Definir permissão de quem pode sincronizar | MT, PARC |
| T-SY-02 | Definir política de concorrência (dois gestores simultâneos) | MOD |
| T-SY-03 | Avaliar necessidade de segregação de `EventoCurso` por empresa | MT, SEB, DISC |

## US-010 — Sacola (T-CR-01…)

| ID | Task | Tags |
|----|------|------|
| T-CR-01 | Especificar geração de código de pedido equivalente a RB-004 | PARC |
| T-CR-02 | Especificar estados do pedido compreensíveis ao usuário | PARC, MOD |
| T-CR-03 | Regra: produtos de empresas diferentes na mesma sacola — permitido ou não? | MT, DISC |

## US-011 — Cupom (T-CP-01…)

| ID | Task | Tags |
|----|------|------|
| T-CP-01 | Matriz de estados do cupom × mensagem ao usuário | PARC |
| T-CP-02 | Regras de combinação cupom × campanha × produto | PARC, DISC |

## US-012 — Checkout (T-CH-01…)

| ID | Task | Tags |
|----|------|------|
| T-CH-01 | Fluxo de estados: pedido → pagamento → venda | PARC |
| T-CH-02 | Definir o que constitui “sucesso” de pagamento no negócio | PARC, MOD |
| T-CH-03 | Mapear quando `efetuarInscricaoPaga` é obrigatório | SEB, PARC |

## US-013 — Termos (T-TM-01…)

| ID | Task | Tags |
|----|------|------|
| T-TM-01 | Lista de termos versionados e vigência | PARC, DISC |
| T-TM-02 | Prova de aceite associada à venda | PARC, MT |

## US-020–021 — IAM (T-IA-01…)

| ID | Task | Tags |
|----|------|------|
| T-IA-01 | Política de senha e recuperação | MOD |
| T-IA-02 | Papéis e permissões por persona admin | MT, PARC |
| T-IA-03 | Comportamento de login social documentado ponta a ponta | PARC, DISC |

## US-022–023 — Cliente e integração (T-CU-01…)

| ID | Task | Tags |
|----|------|------|
| T-CU-01 | Remover fallback “primeira empresa” — especificação de substituto | MT, DEB |
| T-CU-02 | SLA de resposta das consultas CPF/CNPJ | SEB, MOD |
| T-CU-03 | Casos de borda: cliente sem integração disponível | SEB, MOD |

## US-030–031 — Pós-compra (T-PO-01…)

| ID | Task | Tags |
|----|------|------|
| T-PO-01 | Política de re-download e expiração de link | PARC, MOD |
| T-PO-02 | Limites de itens na lista de desejos | PARC |

## US-040–041 — CMS e lead (T-CM-01…)

| ID | Task | Tags |
|----|------|------|
| T-CM-01 | Modelo de conteúdo multi-empresa para FAQ/HTML | MT, PARC |
| T-CM-02 | Corrigir roteamento de e-mail de lead por empresa | MT, DEB |
| T-CM-03 | Templates de e-mail parametrizáveis (substituir copy fixa Sebrae) | MOD, SEB |

## US-050 — Consultoria (T-CO-01…)

| ID | Task | Tags |
|----|------|------|
| T-CO-01 | Regra explícita de `IdEmpresa` em consultoria | MT, DEB |
| T-CO-02 | Estados do fluxo consultoria × catálogo | PARC, SEB |

## US-060–063 — Admin (T-AD-01…)

| ID | Task | Tags |
|----|------|------|
| T-AD-01 | Definir KPIs mínimos do dashboard | MOD, PARC |
| T-AD-02 | Matriz perfil × menu admin | MT, PARC |
| T-AD-03 | Política de upload (tipos, tamanho, antivírus como requisito?) | MOD, PARC |

## US-070–072 — Plataforma / observabilidade (T-PL-01…)

| ID | Task | Tags |
|----|------|------|
| T-PL-01 | Desenhar contrato lógico “Catálogo neutro” consumido pela vitrine | MOD, SEB |
| T-PL-02 | Configuração por empresa: integração Sebrae on/off | MT, SEB |
| T-PL-03 | Definir conjunto mínimo de eventos de negócio a auditar | MOD, MT |

---

## Matriz rápida: visões × tasks

| Visão | Contagem indicativa (tasks acima) |
|-------|-------------------------------------|
| **V1 Paridade** | T-ST-*, T-SR-01, T-PD-*, T-CR-01, T-CP-01, T-CH-01, T-TM-01, T-PO-*, T-AD-02 parcial |
| **V2 Modernização** | T-ST-02, T-SR-03, T-CH-02, T-IA-01, T-CU-02/03, T-PO-01, T-AD-01/03, T-PL-03 |
| **V3 Sebrae** | T-SR-02, T-SY-03, T-CH-03, T-CU-02, T-CM-03, T-CO-02, T-PL-01/02 |
| **V4 Multi-tenant** | T-D02, T-CR-03, T-CU-01, T-CM-01/02, T-CO-01, T-PL-02, T-TM-02 |

---

## Critérios de aceitação das tasks (meta)

Cada task acima, ao ser puxada para sprint, deve ganhar:

- **DoD de negócio:** artefato aprovado (doc, fluxo, mock).
- **Dependência:** referência a UC/RB quando aplicável.
- **Não** incluir nome de framework, linguagem ou provedor cloud neste documento.
