# Domain decisions log — Wave 1 (Tenant, IAM, Catalog)

Status: Active  
Purpose: Registrar decisões de domínio/produto já tomadas por bounded context, sem substituir ADR técnico global.

## Notas de uso

- Este log complementa o ADR 16, mas **não o substitui**.
- Foco: decisões de domínio/produto e seus trade-offs.
- IDs são estáveis para rastreabilidade em reviews e prompts.

## Tenant (BC01)

| ID | Decisão | Motivo | Trade-off | Impacto | Status |
|---|---|---|---|---|---|
| TEN-001 | Tenant é foundation da plataforma | Isolamento e configuração por cliente são requisitos base do SaaS | Exige disciplina transversal em todos os BCs | Orienta modelagem e autenticação de todos os fluxos | Accepted |
| TEN-002 | Slug de tenant é canônico para resolução de contexto | Simplifica roteamento e branding por tenant | Requer governança de unicidade e normalização | Facilita experiência multi-loja e operação | Accepted |
| TEN-003 | Active/inactive com comportamento idempotente | Operações administrativas devem ser seguras em repetição | Mais lógica explícita de estado/transição | Reduz erro operacional em ativa/desativa | Accepted |

## IAM (BC02)

| ID | Decisão | Motivo | Trade-off | Impacto | Status |
|---|---|---|---|---|---|
| IAM-001 | JWT é fonte do contexto autenticado | Evitar lookup redundante e padronizar fronteira de auth | Dependência forte de claims corretas | Simplifica consumo por BCs application layer | Accepted |
| IAM-002 | `tenantId` via claim no JWT | Garantir escopo tenant no ponto de entrada | Rotação/validação de token mais crítica | Isolamento multi-tenant consistente nas APIs | Accepted |
| IAM-003 | `AuthenticatedUser` não é entidade de domínio | Representa contexto de sessão, não regra de negócio persistente | Camada application precisa mapear contexto com clareza | Separa identidade de domínio de autenticação | Accepted |
| IAM-004 | 1 usuário -> 1 tenant (por enquanto) | Simplificar MVP e reduzir ambiguidades de contexto ativo | Menor flexibilidade multi-tenant por usuário | Menos complexidade em autorização inicial | Accepted |

## Catalog (BC03)

| ID | Decisão | Motivo | Trade-off | Impacto | Status |
|---|---|---|---|---|---|
| CAT-001 | Product pertence a tenant | Isolamento de catálogo por cliente | Relatórios globais exigem camada admin separada | Evita vazamento entre lojas | Accepted |
| CAT-002 | Category pertence a tenant | Categorias são contexto comercial da loja | Menor reaproveitamento cross-tenant | Organização de catálogo fica isolada por tenant | Accepted |
| CAT-003 | Product pode existir sem Category | Flexibilizar onboarding de catálogo | Requer tratar `categoryId` nulo em queries/regras | Evita bloquear criação de produto | Accepted |
| CAT-004 | Product referencia Category apenas por `categoryId` | Reduz acoplamento entre agregados | Validação extra em use case | Mantém agregados simples e independentes | Accepted |
| CAT-005 | Validação de Category por tenant via `CategoryRepository.findByIdAndTenant` | Reforçar isolamento no vínculo entre agregados | Mais chamadas de validação | Impede associação a categoria de outro tenant | Accepted |
| CAT-006 | Cross-tenant retorna 404 | Evitar enumeração e expor menos informação | Diferenciação de erro fica menos específica | Política consistente com segurança multi-tenant | Accepted |
| CAT-007 | `tenantId` não vem do request | Fonte de verdade deve ser contexto autenticado | Necessita testes anti-payload malicioso | Reduz risco de spoofing de tenant | Accepted |
| CAT-008 | Listagens são tenant-aware | Toda leitura deve respeitar escopo autenticado | Queries e filtros precisam de padrão disciplinado | Evita vazamento por endpoints de listagem | Accepted |

## Referências

- `.ai/outputs/03-architecture/16-platform-implementation-standards.md`
- `.ai/context/19-multi-tenant-testing-rules.md`
- `.ai/context/20-multi-tenant-validation-checklist-template.md`
- `.ai/context/24-increment-classification.md`
