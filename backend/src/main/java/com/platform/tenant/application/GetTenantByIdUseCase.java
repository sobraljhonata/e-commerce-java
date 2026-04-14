package com.platform.tenant.application;

import com.platform.tenant.domain.Tenant;
import com.platform.tenant.domain.TenantNotFoundException;
import java.util.Objects;
import java.util.UUID;

public class GetTenantByIdUseCase {

    private final TenantRepository tenantRepository;

    public GetTenantByIdUseCase(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant execute(UUID id) {
        UUID resolved = Objects.requireNonNull(id, "id");
        return tenantRepository.findById(resolved).orElseThrow(() -> new TenantNotFoundException(resolved));
    }
}
