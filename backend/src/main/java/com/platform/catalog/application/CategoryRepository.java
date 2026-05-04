package com.platform.catalog.application;

import com.platform.catalog.domain.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  void save(Category category);

  Optional<Category> findByIdAndTenant(UUID tenantId, UUID categoryId);

  List<Category> findAllByTenant(UUID tenantId);
}
