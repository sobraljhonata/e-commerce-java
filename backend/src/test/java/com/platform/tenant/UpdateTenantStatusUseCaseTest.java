package com.platform.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.platform.tenant.adapters.out.persistence.InMemoryTenantRepository;
import com.platform.tenant.application.UpdateTenantStatusUseCase;
import com.platform.tenant.domain.Tenant;
import com.platform.tenant.domain.TenantNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UpdateTenantStatusUseCaseTest {

    @Test
    void should_deactivate_active_tenant() {
        var repository = new InMemoryTenantRepository();
        var id = UUID.randomUUID();
        repository.save(new Tenant(id, "acme", "Acme", true));
        var useCase = new UpdateTenantStatusUseCase(repository);

        var updated = useCase.execute(id, false);

        assertFalse(updated.active());
        assertEquals(id, updated.id());
        assertFalse(repository.findById(id).orElseThrow().active());
    }

    @Test
    void should_activate_inactive_tenant() {
        var repository = new InMemoryTenantRepository();
        var id = UUID.randomUUID();
        repository.save(new Tenant(id, "acme", "Acme", false));
        var useCase = new UpdateTenantStatusUseCase(repository);

        var updated = useCase.execute(id, true);

        assertTrue(updated.active());
    }

    @Test
    void should_be_idempotent_when_deactivating_already_inactive() {
        var repository = new InMemoryTenantRepository();
        var id = UUID.randomUUID();
        var inactive = new Tenant(id, "acme", "Acme", false);
        repository.save(inactive);
        var useCase = new UpdateTenantStatusUseCase(repository);

        var updated = useCase.execute(id, false);

        assertFalse(updated.active());
        assertSame(inactive, updated);
    }

    @Test
    void should_be_idempotent_when_activating_already_active() {
        var repository = new InMemoryTenantRepository();
        var id = UUID.randomUUID();
        var active = new Tenant(id, "acme", "Acme", true);
        repository.save(active);
        var useCase = new UpdateTenantStatusUseCase(repository);

        var updated = useCase.execute(id, true);

        assertTrue(updated.active());
        assertSame(active, updated);
    }

    @Test
    void should_fail_when_tenant_missing() {
        var repository = new InMemoryTenantRepository();
        var useCase = new UpdateTenantStatusUseCase(repository);
        var id = UUID.randomUUID();

        assertThrows(TenantNotFoundException.class, () -> useCase.execute(id, false));
    }
}
