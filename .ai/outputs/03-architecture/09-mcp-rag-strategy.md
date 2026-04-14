# Estratégia MCP e RAG

**Base:** `17-ai-capabilities-scope.md`. **Regra:** IA **fora** do caminho crítico transacional (auth, preço final, captura de pagamento, consistência de pedido).  

---

## Decisão de princípio

| Tema | Decisão |
|------|---------|
| **Core transacional** | Sem LLM; regras determinísticas e testáveis. |
| **MCP** | **POC / capability opcional** para **backoffice e integração assistida**, não para checkout do comprador no MVP. |
| **RAG** | **POC** → possível **capability estratégica** em help center, onboarding de tenant e busca contextual de catálogo **assistida** (não única fonte de verdade). |

---

## MCP — onde gera vantagem real

| Caso de uso | Classificação | Justificativa |
|-------------|---------------|---------------|
| Assistente para operador conectar integrações (Sebrae, PSP) guiando campos e credenciais | **POC → opcional** | Reduz erro humano; ações **confirmadas** por humano. |
| Agente que consulta **APIs internas read-only** (status pedido, logs de integração) | **Opcional** | Acelera suporte N2; requer auth forte e escopo por tenant. |
| MCP no checkout do cliente | **Rejeitado MVP** | Latência e risco de decisão não determinística. |

**Decisão:** gateway MCP (se existir) roda em **rede privada** / VPN, com **OAuth** escopo `admin.support`, **sem** escrita direta em pagamento.  

---

## RAG — onde gera vantagem real

| Caso de uso | Classificação | Fontes permitidas |
|-------------|---------------|-------------------|
| Help center / FAQ por tenant | **POC → estratégico** | Docs aprovados, artigos BC12, políticas publicadas. |
| Onboarding “como configurar minha loja” | **POC** | Guias estáticos + estado **não sensível** do tenant. |
| Busca semântica no catálogo | **Opcional** | **Somente** leitura de índice derivado do catálogo oficial; resultados sempre mostram link para ficha **canônica**. |
| Autorização / “este usuário pode comprar?” | **Proibido** | — |
| Sugestão de preço / desconto automático | **Proibido MVP** | Pode ser **hipótese** futura com human-in-the-loop no backoffice. |

**Decisão:** pipeline RAG com **grounding** obrigatório, **citações** na UI, e **filtro tenant** no retrieval; logs de consulta para auditoria.  

---

## Classificação resumida (obrigatória por iniciativa)

| Tag | Significado |
|-----|-------------|
| **POC** | Time-boxed, sem SLO de produção. |
| **Opcional** | Liga/desliga por feature flag; ausência não bloqueia negócio. |
| **Estratégica** | Diferencial de produto após validação; ainda não no caminho quente. |

---

## Riscos e mitigação

| Risco | Mitigação |
|-------|-----------|
| Alucinação em política comercial | Apenas docs versionados; humano aprova publicação |
| Vazamento cross-tenant no índice | Namespacing de índice por `tenant_id` + testes |
| Custo de embedding | Cache de chunk; modelo dimension adequado; budget |

---

## Recomendações de Wave 1

1. **Não** incluir MCP/RAG no escopo crítico da Wave 1 (ver `11-wave-1-blueprint.md`); reservar **spike** de 1–2 sprints após checkout sandbox estável.  
2. Definir **dono** de dados para RAG (conteúdo aprovado pelo tenant admin).  

---

## Fato / Hipótese

| Tipo | Conteúdo |
|------|----------|
| **Fato** | `17` exige classificação disciplinada — seguido neste doc. |
| **Hipótese** | Maior ROI inicial está em **suporte interno + onboarding**, não em “chat na vitrine”. |
