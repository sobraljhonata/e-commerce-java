package com.platform.tenant.domain;

import java.util.Objects;
import java.util.UUID;

public class Tenant {
  private final UUID id;
  private final String slug;
  private final String displayName;
  private final boolean active;

  public Tenant(UUID id, String slug, String displayName, boolean active) {
    this.id = Objects.requireNonNull(id);
    this.slug = Objects.requireNonNull(slug);
    this.displayName = Objects.requireNonNull(displayName);
    this.active = active;
  }

  /**
   * Canonical slug for persistence and lookup: trim + {@link String#toLowerCase()} (US ASCII). Must
   * match duplicate checks and reads by slug.
   */
  public static String canonicalSlug(String slug) {
    if (slug == null) {
      return "";
    }
    return slug.trim().toLowerCase();
  }

  public static Tenant create(String slug, String displayName) {
    String canonical = canonicalSlug(slug);
    if (canonical.isEmpty()) throw new IllegalArgumentException("slug is required");
    if (displayName == null || displayName.isBlank())
      throw new IllegalArgumentException("displayName is required");
    return new Tenant(UUID.randomUUID(), canonical, displayName.trim(), true);
  }

  /** Idempotent: no-op when already active. */
  public Tenant activate() {
    if (active) {
      return this;
    }
    return new Tenant(id, slug, displayName, true);
  }

  /** Idempotent: no-op when already inactive. */
  public Tenant deactivate() {
    if (!active) {
      return this;
    }
    return new Tenant(id, slug, displayName, false);
  }

  public UUID id() {
    return id;
  }

  public String slug() {
    return slug;
  }

  public String displayName() {
    return displayName;
  }

  public boolean active() {
    return active;
  }
}
