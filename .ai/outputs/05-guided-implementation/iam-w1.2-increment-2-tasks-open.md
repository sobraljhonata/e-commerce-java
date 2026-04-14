# IAM W1.2 — Incremento 2: 3 tasks pequenas (abertas)

Tasks derivadas do fechamento `iam-w1.2-increment-2-closeout.md`. Sem implementação neste arquivo.

---

## Task 1 — Documentação JWT

**Nome sugerido:** `docs(iam): document jwt configuration and security decisions`

**Objetivo:**
- Documentar `JWT_SECRET` (env) e ligação com `platform.security.jwt.secret`.
- Documentar `JWT_EXPIRES_SECONDS` (env) e ligação com `platform.security.jwt.expires-in-seconds`.
- Registrar explicitamente que **defaults do YAML não são adequados para produção** (segredo fraco / previsível).
- Registrar que o Incremento 2 é **baseline de autenticação na borda**, **não** hardening completo (actuator, CORS, política global de rotas, etc.).

**Critérios de aceite (mínimo):**
- [ ] Secção no `README.md` do `backend/` **ou** documento em `.ai/outputs/05-guided-implementation/` referenciado pelo README.
- [ ] Tabela ou lista: variável de ambiente → propriedade Spring → efeito (emissão vs validação).
- [ ] Uma frase de aviso sobre produção + baseline.

**Estimativa:** pequena (≤ 1h).

---

## Task 2 — Teste de round-trip issuer ↔ decoder

**Nome sugerido:** `test(iam): add explicit jwt issuer-decoder roundtrip test`

**Objetivo:**
- Provar por teste de integração (ou teste de slice com contexto Spring) que um token emitido por **`JwtAccessTokenIssuer`** é **aceito** pelo bean **`JwtDecoder`** usado na cadeia de segurança.
- Após decodificar (ou via chamada HTTP autenticada), validar **claims mínimas**: `sub`, `email`, `roles` (coerentes com o usuário seed / login).

**Critérios de aceite (mínimo):**
- [ ] Novo teste com nome/auto-descrição que mencione **round-trip** ou **issuer-decoder contract**.
- [ ] Falha clara se issuer e decoder deixarem de usar o mesmo segredo/algoritmo no futuro.
- [ ] Não duplicar cenário inteiro do BDD se o teste já cobrir o contrato técnico (pode complementar `AdminApiSecurityIntegrationTest` ou classe dedicada).

**Estimativa:** pequena a média (1–2h).

---

## Task 3 — Decisão de segurança Wave 1 (`permitAll`)

**Nome sugerido:** `docs(security): register wave-1 permitAll tradeoff`

**Objetivo:**
- Deixar explícito que **`anyRequest().permitAll()`** na `SecurityFilterChain` é **escolha temporária** da Wave 1 (superfície fora de `/api/admin/**` aberta por omissão).
- Registrar **quando** endurecer: por exemplo primeira rota `/api/**` não pública, preparação para produção, ou política de “deny by default” adotada pelo time.

**Critérios de aceite (mínimo):**
- [ ] ADR curto em `.ai/` **ou** parágrafo no mesmo doc de segurança/JWT da Task 1.
- [ ] Contém: contexto → trade-off → gatilho de revisão → dono sugerido (ex.: chapter arquitetura / próximo incremento IAM).

**Estimativa:** pequena (≤ 1h).

---

## Ordem sugerida

1 → 3 (documentação e decisão ficam alinhadas) → 2 (teste ancora o contrato após docs claras), ou 2 primeiro se prioridade for risco técnico.
