package com.platform.catalog.application;

import com.platform.catalog.domain.Product;
import com.platform.catalog.domain.ProductNotFoundException;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.util.Objects;
import java.util.UUID;

public final class GetProductByIdUseCase {

  private final ProductRepository products;
  private final CurrentUserProvider currentUserProvider;

  public GetProductByIdUseCase(
      ProductRepository products, CurrentUserProvider currentUserProvider) {
    this.products = Objects.requireNonNull(products);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  public Product execute(UUID productId) {
    AuthenticatedUser user =
        currentUserProvider.currentUser().orElseThrow(AuthenticatedContextRequiredException::new);
    UUID tenantId = user.tenantId();
    return products
        .findByIdAndTenant(tenantId, productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
  }
}
