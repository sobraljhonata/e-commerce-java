package com.platform.catalog.adapters.in.web;

import com.platform.catalog.domain.Category;

public record CategoryResponse(String id, String tenantId, String name, boolean active) {

  static CategoryResponse from(Category category) {
    return new CategoryResponse(
        category.id().toString(),
        category.tenantId().toString(),
        category.name(),
        category.active());
  }
}
