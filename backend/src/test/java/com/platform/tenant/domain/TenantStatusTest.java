package com.platform.tenant.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TenantStatusTest {

  @Test
  void activate_is_idempotent_when_already_active() {
    var id = UUID.randomUUID();
    var tenant = new Tenant(id, "a", "A", true);

    assertSame(tenant, tenant.activate());
  }

  @Test
  void deactivate_is_idempotent_when_already_inactive() {
    var id = UUID.randomUUID();
    var tenant = new Tenant(id, "a", "A", false);

    assertSame(tenant, tenant.deactivate());
  }

  @Test
  void activate_changes_inactive_to_active() {
    var id = UUID.randomUUID();
    var inactive = new Tenant(id, "a", "A", false);

    var next = inactive.activate();

    assertTrue(next.active());
    assertSame(id, next.id());
  }

  @Test
  void deactivate_changes_active_to_inactive() {
    var id = UUID.randomUUID();
    var active = new Tenant(id, "a", "A", true);

    var next = active.deactivate();

    assertFalse(next.active());
    assertSame(id, next.id());
  }
}
