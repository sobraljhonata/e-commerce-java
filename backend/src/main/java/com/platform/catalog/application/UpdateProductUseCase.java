package com.platform.catalog.application;

import com.platform.catalog.domain.CategoryInactiveException;
import com.platform.catalog.domain.CategoryNotFoundException;
import com.platform.catalog.domain.Product;
import com.platform.catalog.domain.ProductNotFoundException;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class UpdateProductUseCase {

  private final ProductRepository products;
  private final CategoryRepository categories;
  private final CurrentUserProvider currentUserProvider;

  public UpdateProductUseCase(
      ProductRepository products,
      CategoryRepository categories,
      CurrentUserProvider currentUserProvider) {
    this.products = Objects.requireNonNull(products);
    this.categories = Objects.requireNonNull(categories);
    this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
  }

  public Product execute(
      UUID productId, String name, BigDecimal price, boolean active, UUID categoryIdFromRequest) {
    AuthenticatedUser user =
        currentUserProvider.currentUser().orElseThrow(AuthenticatedContextRequiredException::new);
    UUID tenantId = user.tenantId();
    Product existing =
        products
            .findByIdAndTenant(tenantId, productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    UUID resolvedCategoryId =
        categoryIdFromRequest != null ? categoryIdFromRequest : existing.categoryId();
    requireCategoryInTenant(tenantId, resolvedCategoryId);
    Product updated = existing.update(name, price, active, resolvedCategoryId);
    products.save(updated);
    return updated;
  }

  private void requireCategoryInTenant(UUID tenantId, UUID categoryId) {
    if (categoryId == null) {
      return;
    }
    var category =
        categories
            .findByIdAndTenant(tenantId, categoryId)
            .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    if (!category.active()) {
      throw new CategoryInactiveException(categoryId);
    }
  }
}
