package com.platform.catalog.application;

import com.platform.catalog.domain.CategoryNotFoundException;
import com.platform.catalog.domain.Product;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class CreateProductUseCase {

  private final ProductRepository products;
  private final CategoryRepository categories;
  private final CurrentUserProvider currentUserProvider;

  public CreateProductUseCase(
      ProductRepository products,
      CategoryRepository categories,
      CurrentUserProvider currentUserProvider) {
    this.products = Objects.requireNonNull(products);
    this.categories = Objects.requireNonNull(categories);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  public Product execute(String name, BigDecimal price, boolean active, UUID categoryId) {
    AuthenticatedUser user =
        currentUserProvider.currentUser().orElseThrow(AuthenticatedContextRequiredException::new);
    UUID tenantId = user.tenantId();
    requireCategoryInTenant(tenantId, categoryId);
    Product product = Product.create(tenantId, name, price, active, categoryId);
    products.save(product);
    return product;
  }

  private void requireCategoryInTenant(UUID tenantId, UUID categoryId) {
    if (categoryId == null) {
      return;
    }
    categories
        .findByIdAndTenant(tenantId, categoryId)
        .orElseThrow(() -> new CategoryNotFoundException(categoryId));
  }
}
