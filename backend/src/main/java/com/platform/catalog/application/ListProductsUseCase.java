package com.platform.catalog.application;

import com.platform.catalog.domain.CategoryNotFoundException;
import com.platform.catalog.domain.Product;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ListProductsUseCase {

  private final ProductRepository products;
  private final CategoryRepository categories;
  private final CurrentUserProvider currentUserProvider;

  public ListProductsUseCase(
      ProductRepository products,
      CategoryRepository categories,
      CurrentUserProvider currentUserProvider) {
    this.products = Objects.requireNonNull(products);
    this.categories = Objects.requireNonNull(categories);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  /**
   * Lista produtos do tenant autenticado. Se {@code categoryIdFilter} for não nulo, valida a
   * categoria com {@link CategoryRepository#findByIdAndTenant} e devolve apenas produtos dessa
   * categoria.
   */
  public List<Product> execute(UUID categoryIdFilter) {
    AuthenticatedUser user =
        currentUserProvider.currentUser().orElseThrow(AuthenticatedContextRequiredException::new);
    UUID tenantId = user.tenantId();
    if (categoryIdFilter == null) {
      return products.findAllByTenant(tenantId);
    }
    categories
        .findByIdAndTenant(tenantId, categoryIdFilter)
        .orElseThrow(() -> new CategoryNotFoundException(categoryIdFilter));
    return products.findAllByTenantAndCategoryId(tenantId, categoryIdFilter);
  }
}
