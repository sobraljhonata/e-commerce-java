package com.platform.catalog.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CategoryTest {

  private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");

  @Test
  void create_sets_fields_and_trims_name() {
    Category category = Category.create(TENANT, "  Roupas  ", true);
    assertEquals(TENANT, category.tenantId());
    assertEquals("Roupas", category.name());
    assertTrue(category.active());
  }

  @Test
  void create_rejects_blank_name() {
    assertThrows(IllegalArgumentException.class, () -> Category.create(TENANT, " ", true));
  }
}
