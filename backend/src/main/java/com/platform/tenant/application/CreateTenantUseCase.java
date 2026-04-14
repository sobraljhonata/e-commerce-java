package com.platform.tenant.application;

import com.platform.tenant.domain.DuplicateTenantSlugException;
import com.platform.tenant.domain.Tenant;

public class CreateTenantUseCase {
    private final TenantRepository tenantRepository;

    public CreateTenantUseCase(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant execute(String slug, String displayName) {
        String canonical = Tenant.canonicalSlug(slug);
        tenantRepository.findBySlug(canonical).ifPresent(existing -> {
            throw new DuplicateTenantSlugException(canonical);
        });
        Tenant tenant = Tenant.create(slug, displayName);
        return tenantRepository.save(tenant);
    }
}
