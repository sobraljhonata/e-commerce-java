# Personas e atores — Fase 2

**Fontes:** Fase 1 (`04-fluxos.md`, `02-mapa-de-modulos.md`, `06-analise-multi-tenant.md`), agente *Product Reconstructor*.

---

## 1. Atores do sistema (nível técnico / casos de uso)

| ID | Ator | Descrição | Evidência Fase 1 |
|----|------|-----------|------------------|
| A1 | **Visitante** | Usuário não autenticado na loja | Fluxos F1, F2, F3, F8 (parcial) |
| A2 | **Cliente autenticado** | Usuário com sessão/token na loja | Bearer em interceptor; fluxos F4–F7 |
| A3 | **Usuário administrativo** | Perfil com acesso a funções de gestão | `ApplicationUser`, `Perfil`, `EhAdministrador`; controllers `MenuAdm`, `Empresa`, `Report`, etc. |
| A4 | **Sistema externo Sebrae** | APIs e serviços do ecossistema Sebrae | Integração financeira, cadastro, eventos/cursos, CEP Sebrae (Fase 1) |
| A5 | **Operador de integração (consultoria)** | Ator humano ou fluxo deep-link em “gerenciamento consultoria” | Rota `/gerenciamento-consultoria/...`, F9 |
| A6 | **Serviço em background (hipótese)** | Possível sincronização batch | `winService` na solução; **não confirmado** na Fase 1 — tratar como hipótese (H4 em lacunas) |

---

## 2. Personas (nível produto / negócio)

### P1 — “Ana, visitante em busca de capacitação”

- **Contexto:** Descobre cursos/eventos na vitrine, filtra por tema, cidade ou data.
- **Necessidades:** Ver o que está **disponível** para compra/inscrição, entender detalhes e preço.
- **Dor legada:** Disponibilidade de curso depende de integração + regras locais (RB-003) — risco de lista “vazia” ou confusa se integração falhar.
- **Relação com tenant:** Navegação tipicamente em contexto de **uma** loja/empresa (implícito por URL/config — **hipótese** se não houver seletor explícito no front).

### P2 — “Bruno, comprador (PF ou PJ)”

- **Contexto:** Mantém sacola, aplica cupom, conclui compra, aceita termos quando exigidos.
- **Necessidades:** Checkout claro, confirmação, recibo/histórico; downloads para produtos do tipo download.
- **Dor legada:** E-mail pós-compra com marca Sebrae fixa (RB-008); integração financeira na inscrição paga (RB-009).

### P3 — “Carla, gestora da loja (empresa)”

- **Contexto:** Configura aparência/conteúdo da loja, catálogo, campanhas, parâmetros.
- **Necessidades:** Operar **só nos dados da sua empresa**; relatórios e dashboard operacional.
- **Dor legada:** Modelo com `IdEmpresa` mas atalhos `GetAll().First()` em fluxos que afetam lead/cliente/consultoria (RB-010–012) — risco de vazamento ou gravação no tenant errado.

### P4 — “Diego, integrador / TI”

- **Contexto:** Garante que eventos/cursos e financeiro continuem consistentes com Sebrae.
- **Necessidades:** Visibilidade de falhas de integração, retries, ambiente (homolog/prod).
- **Dor legada:** Acoplamento forte (campos no produto, SQL em serviços, constantes de pagamento).

### P5 — “Elena, lead / interessada”

- **Contexto:** Deixa cadastro simplificado (lead).
- **Necessidades:** Confiança de que o contato vai para a **empresa correta**.
- **Dor legada:** Lead associado à primeira empresa do repositório (RB-012).

---

## 3. Matriz ator × capacidade (resumo)

Legenda: ● primário, ○ secundário, — não evidenciado na Fase 1.

| Capacidade (alto nível) | A1 Visitante | A2 Cliente | A3 Admin | A4 Sebrae |
|-------------------------|:------------:|:----------:|:--------:|:---------:|
| Vitrine / home | ● | ○ | ○ | — |
| Busca / catálogo cursos-eventos | ● | ○ | ○ | ○ |
| Detalhe produto | ● | ○ | ○ | ○ |
| Sacola / pedido | ○ | ● | ○ | — |
| Checkout / venda | — | ● | ○ | ● |
| Conta / cadastro | ○ | ● | ○ | ● |
| Pós-compra / downloads / desejos | — | ● | ○ | — |
| CMS / FAQ / Fale Conosco | ● | ○ | ● | — |
| Gestão empresa / parâmetros | — | — | ● | — |
| Sincronização evento/produto | — | — | ● | ● |
| Lead | ● | — | ● | — |

---

## 4. Hipóteses e discovery (não personas)

- **H-AUTH:** Papel exato de login social e mapeamento perfil ↔ empresa — endpoints no front; regras servidor não fechadas na Fase 1 para todos os controllers.
- **H-ADMIN:** Quem acessa `/admin` e quais perfis de menu existem — requer inventário de `MenuAdm` + telas (não detalhado na Fase 1).
