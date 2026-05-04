package com.platform.catalog.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductTest {

  private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final UUID CAT = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

  @Test
  void create_sets_fields_and_defaults_active() {
    Product p = Product.create(TENANT, "  Camiseta  ", new BigDecimal("29.99"), true, null);
    assertEquals("Camiseta", p.name());
    assertEquals(TENANT, p.tenantId());
    assertEquals(0, new BigDecimal("29.99").compareTo(p.price()));
    assertTrue(p.active());
    assertNull(p.categoryId());
  }

  @Test
  void create_rejects_blank_name() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Product.create(TENANT, "  ", BigDecimal.ONE, true, null));
  }

  @Test
  void create_rejects_non_positive_price() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Product.create(TENANT, "X", BigDecimal.ZERO, true, null));
  }

  @Test
  void update_preserves_id_and_tenant_and_applies_new_fields() {
    UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product p = Product.restore(id, TENANT, "A", BigDecimal.ONE, true, CAT);
    Product u = p.update("B", new BigDecimal("3.50"), false, CAT);
    assertEquals(id, u.id());
    assertEquals(TENANT, u.tenantId());
    assertEquals("B", u.name());
    assertEquals(0, new BigDecimal("3.50").compareTo(u.price()));
    assertEquals(false, u.active());
    assertEquals(CAT, u.categoryId());
  }

  @Test
  void update_rejects_invalid_name() {
    Product p = Product.create(TENANT, "Ok", BigDecimal.ONE, true, null);
    assertThrows(IllegalArgumentException.class, () -> p.update("  ", BigDecimal.ONE, true, null));
  }
}
