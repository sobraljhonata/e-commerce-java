package com.platform.tenant.application;

import com.platform.tenant.domain.Tenant;
import com.platform.tenant.domain.TenantNotFoundBySlugException;

public class GetTenantBySlugUseCase {

    private final TenantRepository tenantRepository;

    public GetTenantBySlugUseCase(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant execute(String slug) {
        String canonical = Tenant.canonicalSlug(slug);
        if (canonical.isEmpty()) {
            throw new IllegalArgumentException("slug is required");
        }
        return tenantRepository.findBySlug(canonical).orElseThrow(() -> new TenantNotFoundBySlugException(canonical));
    }
}
