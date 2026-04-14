package com.platform.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.platform.tenant.adapters.out.persistence.InMemoryTenantRepository;
import com.platform.tenant.application.GetTenantBySlugUseCase;
import com.platform.tenant.domain.Tenant;
import com.platform.tenant.domain.TenantNotFoundBySlugException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetTenantBySlugUseCaseTest {

    @Test
    void should_return_tenant_when_slug_matches_canonical_form() {
        var repository = new InMemoryTenantRepository();
        var id = UUID.randomUUID();
        repository.save(new Tenant(id, "acme", "Acme Corp", true));
        var useCase = new GetTenantBySlugUseCase(repository);

        var found = useCase.execute("  Acme ");

        assertEquals(id, found.id());
        assertEquals("acme", found.slug());
    }

    @Test
    void should_fail_when_no_tenant_for_slug() {
        var repository = new InMemoryTenantRepository();
        var useCase = new GetTenantBySlugUseCase(repository);

        var ex = assertThrows(TenantNotFoundBySlugException.class, () -> useCase.execute("missing"));

        assertEquals("missing", ex.slug());
    }

    @Test
    void should_fail_when_slug_blank_after_normalization() {
        var repository = new InMemoryTenantRepository();
        var useCase = new GetTenantBySlugUseCase(repository);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute("   "));
    }
}
