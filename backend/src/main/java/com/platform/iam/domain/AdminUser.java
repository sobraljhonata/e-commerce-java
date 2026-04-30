package com.platform.iam.domain;

import java.util.Objects;
import java.util.UUID;

/** Usuário administrativo da plataforma (agregado mínimo W1.2). */
public final class AdminUser {

  private final UUID id;
  private final UUID tenantId;
  private final String email;
  private final String passwordHash;
  private final boolean active;
  private final AdminRole role;

  public AdminUser(
      UUID id, UUID tenantId, String email, String passwordHash, boolean active, AdminRole role) {
    this.id = Objects.requireNonNull(id);
    this.tenantId = Objects.requireNonNull(tenantId);
    this.email = Objects.requireNonNull(email);
    this.passwordHash = Objects.requireNonNull(passwordHash);
    this.active = active;
    this.role = Objects.requireNonNull(role);
  }

  /** Email canônico para lookup: trim + lower case (ASCII). */
  public static String canonicalEmail(String email) {
    if (email == null) {
      return "";
    }
    return email.trim().toLowerCase();
  }

  /** Reconstituição a partir de persistência (sem validação de senha em texto plano). */
  public static AdminUser restore(
      UUID id, UUID tenantId, String email, String passwordHash, boolean active, AdminRole role) {
    String canonical = canonicalEmail(email);
    if (canonical.isEmpty()) {
      throw new IllegalArgumentException("email is required");
    }
    return new AdminUser(id, tenantId, canonical, passwordHash, active, role);
  }

  public UUID id() {
    return id;
  }

  /** Contexto de tenant para emissão do JWT (não implica lookup no BC Tenant). */
  public UUID tenantId() {
    return tenantId;
  }

  public String email() {
    return email;
  }

  public String passwordHash() {
    return passwordHash;
  }

  public boolean active() {
    return active;
  }

  public AdminRole role() {
    return role;
  }
}
