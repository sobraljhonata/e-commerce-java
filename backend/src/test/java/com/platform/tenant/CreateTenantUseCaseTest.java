package com.platform.tenant;

import static org.junit.jupiter.api.Assertions.*;

import com.platform.tenant.adapters.out.persistence.InMemoryTenantRepository;
import com.platform.tenant.application.CreateTenantUseCase;
import com.platform.tenant.domain.DuplicateTenantSlugException;
import org.junit.jupiter.api.Test;

class CreateTenantUseCaseTest {

  @Test
  void should_create_tenant_when_slug_is_unique() {
    var repository = new InMemoryTenantRepository();
    var useCase = new CreateTenantUseCase(repository);

    var tenant = useCase.execute("tenant-a", "Tenant A");

    assertNotNull(tenant.id());
    assertEquals("tenant-a", tenant.slug());
    assertEquals("Tenant A", tenant.displayName());
    assertTrue(tenant.active());
  }

  @Test
  void should_fail_when_slug_already_exists() {
    var repository = new InMemoryTenantRepository();
    var useCase = new CreateTenantUseCase(repository);
    useCase.execute("tenant-a", "Tenant A");

    var ex =
        assertThrows(
            DuplicateTenantSlugException.class, () -> useCase.execute("tenant-a", "Duplicate"));

    assertEquals("tenant-a", ex.slug());
  }

  @Test
  void should_fail_when_slug_already_exists_case_insensitive() {
    var repository = new InMemoryTenantRepository();
    var useCase = new CreateTenantUseCase(repository);
    useCase.execute("Tenant-A", "Tenant A");

    var ex =
        assertThrows(
            DuplicateTenantSlugException.class, () -> useCase.execute("tenant-a", "Duplicate"));

    assertEquals("tenant-a", ex.slug());
  }

  @Test
  void should_fail_when_slug_is_duplicate_case_insensitive() {
    var repository = new InMemoryTenantRepository();
    var useCase = new CreateTenantUseCase(repository);
    useCase.execute("tenant-a", "Tenant A");

    var ex =
        assertThrows(
            DuplicateTenantSlugException.class, () -> useCase.execute("TENANT-A", "Duplicate"));

    assertEquals("tenant-a", ex.slug());
  }
}
