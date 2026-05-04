package com.platform.catalog.application;

import com.platform.catalog.domain.Category;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ListCategoriesUseCase {

  private final CategoryRepository categories;
  private final CurrentUserProvider currentUserProvider;

  public ListCategoriesUseCase(
      CategoryRepository categories, CurrentUserProvider currentUserProvider) {
    this.categories = Objects.requireNonNull(categories);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  public List<Category> execute() {
    AuthenticatedUser user =
        currentUserProvider.currentUser().orElseThrow(AuthenticatedContextRequiredException::new);
    UUID tenantId = user.tenantId();
    return categories.findAllByTenant(tenantId);
  }
}
