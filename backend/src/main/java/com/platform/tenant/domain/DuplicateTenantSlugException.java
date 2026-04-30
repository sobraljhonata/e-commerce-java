package com.platform.tenant.domain;

import java.util.Objects;

/** Raised when a new tenant cannot be registered because the slug is already taken. */
public final class DuplicateTenantSlugException extends RuntimeException {

  private final String slug;

  public DuplicateTenantSlugException(String slug) {
    super("Tenant slug already exists: " + Objects.requireNonNull(slug, "slug"));
    this.slug = slug;
  }

  public String slug() {
    return slug;
  }
}
