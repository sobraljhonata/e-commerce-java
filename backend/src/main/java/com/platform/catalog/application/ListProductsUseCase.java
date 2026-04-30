package com.platform.catalog.application;

import com.platform.catalog.domain.Product;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ListProductsUseCase {

  private final ProductRepository products;
  private final CurrentUserProvider currentUserProvider;

  public ListProductsUseCase(ProductRepository products, CurrentUserProvider currentUserProvider) {
    this.products = Objects.requireNonNull(products);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  public List<Product> execute() {
    AuthenticatedUser user =
        currentUserProvider.currentUser().orElseThrow(AuthenticatedContextRequiredException::new);
    UUID tenantId = user.tenantId();
    return products.findAllByTenant(tenantId);
  }
}
