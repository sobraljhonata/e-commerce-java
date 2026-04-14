package com.platform.tenant.application;

import com.platform.tenant.domain.Tenant;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(UUID id);
    Optional<Tenant> findBySlug(String slug);
}
