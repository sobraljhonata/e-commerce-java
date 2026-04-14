package com.platform.tenant.domain;

import java.util.Objects;

/**
 * Raised when no tenant exists for the given canonical slug.
 */
public final class TenantNotFoundBySlugException extends RuntimeException {

    private final String slug;

    public TenantNotFoundBySlugException(String canonicalSlug) {
        super("Tenant not found for slug: " + Objects.requireNonNull(canonicalSlug, "slug"));
        this.slug = canonicalSlug;
    }

    public String slug() {
        return slug;
    }
}
