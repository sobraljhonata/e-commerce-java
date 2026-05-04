package com.platform.catalog.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.platform.catalog.domain.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryProductRepositoryTest {

  private static final UUID TENANT_A = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final UUID TENANT_B = UUID.fromString("22222222-3333-4444-5555-666666666666");
  private static final UUID CAT = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

  @Test
  void findAllByTenant_excludes_other_tenant_products() {
    var repo = new InMemoryProductRepository();
    UUID idA = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    UUID idB = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    repo.save(Product.restore(idA, TENANT_A, "a", BigDecimal.ONE, true, null));
    repo.save(Product.restore(idB, TENANT_B, "b", BigDecimal.ONE, true, null));

    List<Product> list = repo.findAllByTenant(TENANT_A);
    assertEquals(1, list.size());
    assertEquals(idA, list.get(0).id());
  }

  @Test
  void findByIdAndTenant_returns_empty_when_other_tenant() {
    var repo = new InMemoryProductRepository();
    UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product p = Product.restore(id, TENANT_A, "X", BigDecimal.ONE, true, null);
    repo.save(p);

    assertTrue(repo.findByIdAndTenant(TENANT_B, id).isEmpty());
  }

  @Test
  void findByIdAndTenant_returns_product_when_tenant_matches() {
    var repo = new InMemoryProductRepository();
    UUID id = UUID.randomUUID();
    Product p = Product.restore(id, TENANT_A, "Y", new BigDecimal("2.00"), true, null);
    repo.save(p);

    assertEquals(p, repo.findByIdAndTenant(TENANT_A, id).orElseThrow());
  }

  @Test
  void save_overwrites_same_id_after_domain_update() {
    var repo = new InMemoryProductRepository();
    UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product first = Product.restore(id, TENANT_A, "A", BigDecimal.ONE, true, null);
    repo.save(first);
    Product second = first.update("B", new BigDecimal("2.00"), false, null);
    repo.save(second);

    Product loaded = repo.findByIdAndTenant(TENANT_A, id).orElseThrow();
    assertEquals("B", loaded.name());
    assertEquals(false, loaded.active());
  }

  @Test
  void findAllByTenantAndCategoryId_excludes_other_category_and_other_tenant() {
    var repo = new InMemoryProductRepository();
    UUID idMatch = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    UUID idOtherCat = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    UUID idOtherTenant = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    UUID otherCat = UUID.fromString("11111111-1111-1111-1111-111111111111");
    repo.save(Product.restore(idMatch, TENANT_A, "m", BigDecimal.ONE, true, CAT));
    repo.save(Product.restore(idOtherCat, TENANT_A, "o", BigDecimal.ONE, true, otherCat));
    repo.save(Product.restore(idOtherTenant, TENANT_B, "b", BigDecimal.ONE, true, CAT));

    List<Product> list = repo.findAllByTenantAndCategoryId(TENANT_A, CAT);
    assertEquals(1, list.size());
    assertEquals(idMatch, list.get(0).id());
  }
}
