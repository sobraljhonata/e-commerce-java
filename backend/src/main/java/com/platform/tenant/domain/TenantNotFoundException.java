package com.platform.tenant.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Raised when a tenant cannot be resolved by identifier.
 */
public final class TenantNotFoundException extends RuntimeException {

    private final UUID tenantId;

    public TenantNotFoundException(UUID tenantId) {
        super("Tenant not found: " + Objects.requireNonNull(tenantId, "tenantId"));
        this.tenantId = tenantId;
    }

    public UUID tenantId() {
        return tenantId;
    }
}
