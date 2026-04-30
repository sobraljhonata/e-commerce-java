package com.platform.catalog.application;

import com.platform.catalog.domain.Category;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.util.Objects;
import java.util.UUID;

public final class CreateCategoryUseCase {

  private final CategoryRepository categories;
  private final CurrentUserProvider currentUserProvider;

  public CreateCategoryUseCase(
      CategoryRepository categories, CurrentUserProvider currentUserProvider) {
    this.categories = Objects.requireNonNull(categories);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  public Category execute(String name, boolean active) {
    AuthenticatedUser user =
        currentUserProvider.currentUser().orElseThrow(AuthenticatedContextRequiredException::new);
    UUID tenantId = user.tenantId();
    Category category = Category.create(tenantId, name, active);
    categories.save(category);
    return category;
  }
}
