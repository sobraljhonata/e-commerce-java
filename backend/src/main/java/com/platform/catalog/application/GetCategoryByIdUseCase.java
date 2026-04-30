package com.platform.catalog.application;

import com.platform.catalog.domain.Category;
import com.platform.catalog.domain.CategoryNotFoundException;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.util.Objects;
import java.util.UUID;

public final class GetCategoryByIdUseCase {

  private final CategoryRepository categories;
  private final CurrentUserProvider currentUserProvider;

  public GetCategoryByIdUseCase(
      CategoryRepository categories, CurrentUserProvider currentUserProvider) {
    this.categories = Objects.requireNonNull(categories);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  public Category execute(UUID categoryId) {
    AuthenticatedUser user =
        currentUserProvider.currentUser().orElseThrow(AuthenticatedContextRequiredException::new);
    UUID tenantId = user.tenantId();
    return categories
        .findByIdAndTenant(tenantId, categoryId)
        .orElseThrow(() -> new CategoryNotFoundException(categoryId));
  }
}
