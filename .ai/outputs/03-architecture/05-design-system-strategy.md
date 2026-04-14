# Estratégia de design system

**Contexto:** `18-design-system-product-principles.md` — DS como **capability de produto e plataforma**.  
**Decisão:** um **design system de plataforma** (neutro) + **camada de theming por tenant** (sem fork de componentes por cliente).  

---

## Princípios fechados

1. **Foundations antes de páginas:** tokens → primitives → componentes → padrões → compostos → domínio.  
2. **Acessibilidade não negociável:** WCAG 2.2 AA como alvo; contraste e foco validados em CI visual opcional.  
3. **Multi-tenant por token semântico**, não por cópia de biblioteca.  

---

## Stack de front (alinhamento)

| Item | Escolha |
|------|---------|
| **Framework UI base** | Angular Material **ou** PrimeNG — **decisão Wave 1** conforme afinidade do time; ambos permitem theming. |
| **Tokens** | Style Dictionary ou Tokens Studio export → CSS custom properties + tipos TS. |
| **Documentação** | Storybook para componentes de plataforma. |
| **Ícones** | Conjunto único (ex.: Phosphor/Material) — troca rara. |

**Recomendação:** escolher **um** kit base para reduzir custo; customizar via tokens.  

---

## Estrutura de camadas

| Camada | Conteúdo | Dono |
|--------|----------|------|
| **L0 Tokens** | cor, espaçamento, tipografia, raio, elevação | Plataforma |
| **L1 Semantic tokens** | `--color-surface`, `--color-primary` | Plataforma |
| **L2 Primitives** | Button, Input, Card (sem regra de negócio) | Plataforma |
| **L3 Padrões** | formulários, listagens, empty states | Plataforma |
| **L4 Compostos** | header loja, card de produto genérico | Plataforma |
| **L5 Domínio** | “Card curso”, “Inscrição evento”, checkout steps | Produto vertical |

**Decisão:** L0–L4 publicados como pacote interno `@platform/ui`; L5 fica no app storefront mas **consome** apenas L0–L4.  

---

## Theming por tenant

| Mecanismo | Detalhe |
|-----------|---------|
| **Fonte da verdade** | BC01 expõe `theme` (subset seguro de tokens override). |
| **Aplicação** | CSS variables injetadas no `index.html` ou carregadas na inicialização da SPA. |
| **Limites** | Apenas paleta, logo, radius leve — **não** permitir CSS arbitrário de cliente no MVP (risco XSS/brand break). |

---

## Jornadas latency-sensitive (UX)

- **Checkout:** fluxo linear, mínimo de etapas, feedback imediato de validação, skeletons em catálogo.  
- **Vitrine:** imagens otimizadas, lazy load, CDN (CloudFront).  

---

## Governança

| Regra | Implementação |
|-------|----------------|
| **Breaking change** | Semver do pacote `@platform/ui` + changelog. |
| **Contribuição** | PR + Storybook + a11y checklist. |
| **Dark mode** | **Hipótese:** opcional pós-MVP; tokens preparados desde L0. |

---

## Fato / Decisão / Risco

| Tipo | Conteúdo |
|------|----------|
| **Fato** | Legado misturou CSS global e templates difíceis de reutilizar. |
| **Decisão** | DS centralizado + tenant theme controlado. |
| **Risco** | “Páginas primeiro” — mitigar com Definition of Ready para features UI. |
