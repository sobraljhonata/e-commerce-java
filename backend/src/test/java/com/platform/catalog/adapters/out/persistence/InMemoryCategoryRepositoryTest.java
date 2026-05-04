package com.platform.catalog.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.platform.catalog.domain.Category;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryCategoryRepositoryTest {

  private static final UUID TENANT_A = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final UUID TENANT_B = UUID.fromString("22222222-3333-4444-5555-666666666666");

  @Test
  void findByIdAndTenant_returns_empty_when_other_tenant() {
    var repo = new InMemoryCategoryRepository();
    UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Category category = Category.restore(id, TENANT_A, "A", true);
    repo.save(category);

    assertTrue(repo.findByIdAndTenant(TENANT_B, id).isEmpty());
  }

  @Test
  void findByIdAndTenant_returns_category_when_tenant_matches() {
    var repo = new InMemoryCategoryRepository();
    UUID id = UUID.randomUUID();
    Category category = Category.restore(id, TENANT_A, "B", true);
    repo.save(category);

    assertEquals(category, repo.findByIdAndTenant(TENANT_A, id).orElseThrow());
  }

  @Test
  void findAllByTenant_excludes_categories_from_other_tenants() {
    var repo = new InMemoryCategoryRepository();
    UUID idA = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    UUID idB = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    repo.save(Category.restore(idA, TENANT_A, "A", true));
    repo.save(Category.restore(idB, TENANT_B, "B", true));

    List<Category> list = repo.findAllByTenant(TENANT_A);
    assertEquals(1, list.size());
    assertEquals(idA, list.get(0).id());
  }
}
