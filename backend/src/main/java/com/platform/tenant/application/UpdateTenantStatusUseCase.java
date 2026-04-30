package com.platform.tenant.application;

import com.platform.tenant.domain.Tenant;
import com.platform.tenant.domain.TenantNotFoundException;
import java.util.Objects;
import java.util.UUID;

public class UpdateTenantStatusUseCase {

  private final TenantRepository tenantRepository;

  public UpdateTenantStatusUseCase(TenantRepository tenantRepository) {
    this.tenantRepository = tenantRepository;
  }

  public Tenant execute(UUID id, boolean active) {
    UUID resolved = Objects.requireNonNull(id, "id");
    Tenant current =
        tenantRepository
            .findById(resolved)
            .orElseThrow(() -> new TenantNotFoundException(resolved));
    Tenant next = active ? current.activate() : current.deactivate();
    return tenantRepository.save(next);
  }
}
