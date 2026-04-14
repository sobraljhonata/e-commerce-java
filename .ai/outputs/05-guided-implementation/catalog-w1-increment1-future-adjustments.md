# BC Catalog (W1) — Ajustes adiados após o Incremento 1

**Contexto:** revisão crítica pós-implementação do cadastro de produto (`POST /api/admin/products`), repositório em memória e `Product.create` / `Product.restore`.

**Quando aplicar:** na introdução de **persistência real** e/ou de **operações de leitura** (get/list) no Catalog — não são bloqueantes para o escopo atual (só criação, in-memory).

---

## F-AJ-CAT-01 — Reidratação do agregado `Product` (`restore`)

**Situação hoje:** `Product.create` valida nome, preço positivo e limite de precisão; `Product.restore` apenas exige não-nulos no construtor, sem reaplicar as mesmas invariantes.

**Risco se não tratar:** estado inválido pode entrar no agregado vindo de mapeamento incorreto, migração ou corrupção de dados.

**Opções de desenho (escolher na evolução):**

1. **Endurecer `restore`:** reaplicar invariantes compartilhadas com `create` (extrair validadores comuns para evitar divergência).
2. **Confiar no persistido:** manter `restore` como reconstituição de estado já validado na escrita; tratar violações como erro de infraestrutura ou migração.

**Critério de gatilho:** primeira versão de **adapter de persistência** (JDBC/JPA) ou necessidade de carregar dados legados com regras que mudaram.

---

## F-AJ-CAT-02 — Escopo multi-tenant no `ProductRepository` (leituras)

**Atualização (Incremento 2):** implementado `findByIdAndTenant(tenantId, productId)` com **mapa por id** + verificação explícita de `tenantId` no `InMemoryProductRepository`; `GetProductByIdUseCase` obtém o tenant só via `CurrentUserProvider`. **Listagem / outras leituras** ainda devem repetir o mesmo padrão ao entrarem no backlog.

**Situação antes do I2:** repositório indexava por `product.id` apenas; suficiente para **só salvar** na Wave 1.

**Risco se não tratar ao ler:** qualquer `get(id)` sem amarrar ao **tenant do contexto autenticado** pode expor recurso de outro tenant se o cliente obtiver um UUID alheio (problema de **autorização de acesso**, não só de colisão de chave).

**Diretrizes para leituras futuras (listagem, etc.):**

- Contratos de leitura devem incluir **`tenantId`** (do contexto JWT / use case).
- Implementação pode evoluir para **chave composta** no armazenamento quando houver persistência real, mantendo o contrato tenant-aware.

---

*Registro para backlog técnico; não substitui ADR formal se a equipe padronizar decisões de persistência/repositório em documento separado.*
