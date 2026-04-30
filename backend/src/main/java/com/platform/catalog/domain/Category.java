package com.platform.catalog.domain;

import java.util.Objects;
import java.util.UUID;

/** Categoria de catálogo no escopo de um tenant (Wave 1). */
public final class Category {

    private static final int MAX_NAME_LENGTH = 120;

    private final UUID id;
    private final UUID tenantId;
    private final String name;
    private final boolean active;

    private Category(UUID id, UUID tenantId, String name, boolean active) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.name = Objects.requireNonNull(name);
        this.active = active;
    }

    public static Category create(UUID tenantId, String name, boolean active) {
        Objects.requireNonNull(tenantId, "tenantId");
        return new Category(UUID.randomUUID(), tenantId, requireName(name), active);
    }

    public static Category restore(UUID id, UUID tenantId, String name, boolean active) {
        return new Category(id, tenantId, name, active);
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("name exceeds max length");
        }
        return trimmed;
    }

    public UUID id() {
        return id;
    }

    public UUID tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }

    public boolean active() {
        return active;
    }
}
