package com.platform.catalog.application;

import java.util.Optional;
import java.util.UUID;

import com.platform.catalog.domain.Category;

public interface CategoryRepository {

    void save(Category category);

    Optional<Category> findByIdAndTenant(UUID tenantId, UUID categoryId);
}
