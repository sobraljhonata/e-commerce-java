package com.platform.catalog.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/** Produto de catálogo no escopo de um tenant (agregado mínimo Wave 1). */
public final class Product {

    private static final int MAX_NAME_LENGTH = 255;

    private final UUID id;
    private final UUID tenantId;
    private final String name;
    private final BigDecimal price;
    private final boolean active;

    private Product(UUID id, UUID tenantId, String name, BigDecimal price, boolean active) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.name = Objects.requireNonNull(name);
        this.price = Objects.requireNonNull(price);
        this.active = active;
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

    private static BigDecimal requirePrice(BigDecimal price) {
        Objects.requireNonNull(price, "price");
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("price must be positive");
        }
        if (price.precision() > 12) {
            throw new IllegalArgumentException("price precision exceeds allowed range");
        }
        return price;
    }

    /**
     * Novo produto no tenant indicado. {@code price} deve ser &gt; 0; escala excessiva é rejeitada
     * para manter valores comerciais simples na Wave 1.
     */
    public static Product create(UUID tenantId, String name, BigDecimal price, boolean active) {
        Objects.requireNonNull(tenantId, "tenantId");
        return new Product(UUID.randomUUID(), tenantId, requireName(name), requirePrice(price), active);
    }

    /**
     * Atualização controlada dos atributos mutáveis. {@code id} e {@code tenantId} permanecem os mesmos.
     */
    public Product update(String name, BigDecimal price, boolean active) {
        return new Product(this.id, this.tenantId, requireName(name), requirePrice(price), active);
    }

    /** Reconstituição (ex.: persistência futura). */
    public static Product restore(UUID id, UUID tenantId, String name, BigDecimal price, boolean active) {
        return new Product(id, tenantId, name, price, active);
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

    public BigDecimal price() {
        return price;
    }

    public boolean active() {
        return active;
    }
}
