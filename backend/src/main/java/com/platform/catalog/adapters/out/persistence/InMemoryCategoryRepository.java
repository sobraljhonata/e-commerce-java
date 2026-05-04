package com.platform.catalog.adapters.out.persistence;

import com.platform.catalog.application.CategoryRepository;
import com.platform.catalog.domain.Category;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryCategoryRepository implements CategoryRepository {

  private final Map<UUID, Category> byId = new ConcurrentHashMap<>();

  public void clear() {
    byId.clear();
  }

  @Override
  public void save(Category category) {
    byId.put(category.id(), category);
  }

  @Override
  public Optional<Category> findByIdAndTenant(UUID tenantId, UUID categoryId) {
    Category found = byId.get(categoryId);
    if (found == null) {
      return Optional.empty();
    }
    if (!found.tenantId().equals(tenantId)) {
      return Optional.empty();
    }
    return Optional.of(found);
  }

  @Override
  public List<Category> findAllByTenant(UUID tenantId) {
    return byId.values().stream()
        .filter(c -> c.tenantId().equals(tenantId))
        .sorted(Comparator.comparing(Category::id))
        .toList();
  }
}
