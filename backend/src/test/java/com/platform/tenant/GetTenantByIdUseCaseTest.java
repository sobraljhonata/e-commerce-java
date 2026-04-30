package com.platform.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.platform.tenant.adapters.out.persistence.InMemoryTenantRepository;
import com.platform.tenant.application.GetTenantByIdUseCase;
import com.platform.tenant.domain.Tenant;
import com.platform.tenant.domain.TenantNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetTenantByIdUseCaseTest {

  @Test
  void should_return_tenant_when_present() {
    var repository = new InMemoryTenantRepository();
    var id = UUID.randomUUID();
    var existing = new Tenant(id, "acme", "Acme", true);
    repository.save(existing);
    var useCase = new GetTenantByIdUseCase(repository);

    var found = useCase.execute(id);

    assertEquals(id, found.id());
    assertEquals("acme", found.slug());
  }

  @Test
  void should_fail_when_tenant_missing() {
    var repository = new InMemoryTenantRepository();
    var useCase = new GetTenantByIdUseCase(repository);
    var id = UUID.randomUUID();

    var ex = assertThrows(TenantNotFoundException.class, () -> useCase.execute(id));

    assertEquals(id, ex.tenantId());
  }
}
