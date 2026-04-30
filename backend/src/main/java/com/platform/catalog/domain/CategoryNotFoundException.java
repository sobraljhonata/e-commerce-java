package com.platform.catalog.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Nenhuma categoria acessível para o tenant atual com o identificador informado (inexistente ou de
 * outro tenant).
 */
public final class CategoryNotFoundException extends RuntimeException {

  private final UUID categoryId;

  public CategoryNotFoundException(UUID categoryId) {
    super("Category not found: " + Objects.requireNonNull(categoryId, "categoryId"));
    this.categoryId = categoryId;
  }

  public UUID categoryId() {
    return categoryId;
  }
}
