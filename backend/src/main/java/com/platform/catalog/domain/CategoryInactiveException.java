package com.platform.catalog.domain;

import java.util.Objects;
import java.util.UUID;

/** Categoria existe no tenant, mas está inativa para associação em produto. */
public final class CategoryInactiveException extends RuntimeException {

  private final UUID categoryId;

  public CategoryInactiveException(UUID categoryId) {
    super("Category is inactive: " + Objects.requireNonNull(categoryId, "categoryId"));
    this.categoryId = categoryId;
  }

  public UUID categoryId() {
    return categoryId;
  }
}
