package com.platform.iam.adapters.out.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.platform.iam.application.AdminUserRepository;
import com.platform.iam.domain.AdminRole;
import com.platform.iam.domain.AdminUser;

/**
 * Repositório em memória com seed de um usuário para desenvolvimento.
 *
 * <p>Credenciais seed: constantes {@code SEED_EMAIL} e {@code SEED_PASSWORD} (apenas ambientes não
 * produtivos).
 */
public final class InMemoryAdminUserRepository implements AdminUserRepository {

    /** Email do usuário administrativo inicial. */
    public static final String SEED_EMAIL = "admin@local.dev";

    /** Senha em texto plano do usuário seed (somente para dev/local). */
    public static final String SEED_PASSWORD = "admin-secret";

    private static final UUID SEED_USER_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Tenant de contexto do usuário seed (claim {@code tenantId} no JWT). */
    public static final UUID SEED_TENANT_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    private final Map<String, AdminUser> byEmail = new ConcurrentHashMap<>();

    /** Construtor padrão: inclui o usuário seed de desenvolvimento. */
    public InMemoryAdminUserRepository() {
        this(true);
    }

    /** Repositório sem usuários (útil em testes unitários). */
    public static InMemoryAdminUserRepository empty() {
        return new InMemoryAdminUserRepository(false);
    }

    private InMemoryAdminUserRepository(boolean seedDevUser) {
        if (seedDevUser) {
            var encoder = new BCryptPasswordEncoder();
            save(
                AdminUser.restore(
                    SEED_USER_ID,
                    SEED_TENANT_ID,
                    SEED_EMAIL,
                    encoder.encode(SEED_PASSWORD),
                    true,
                    AdminRole.PLATFORM_ADMIN));
        }
    }

    @Override
    public Optional<AdminUser> findByEmail(String canonicalEmail) {
        return Optional.ofNullable(byEmail.get(canonicalEmail));
    }

    @Override
    public void save(AdminUser user) {
        byEmail.put(user.email(), user);
    }
}
