package com.platform.catalog.application;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import com.platform.catalog.domain.Product;
import com.platform.catalog.domain.ProductNotFoundException;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;

public final class UpdateProductUseCase {

    private final ProductRepository products;
    private final CurrentUserProvider currentUserProvider;

    public UpdateProductUseCase(ProductRepository products, CurrentUserProvider currentUserProvider) {
        this.products = Objects.requireNonNull(products);
        this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
    }

    public Product execute(UUID productId, String name, BigDecimal price, boolean active) {
        AuthenticatedUser user =
                currentUserProvider
                        .currentUser()
                        .orElseThrow(AuthenticatedContextRequiredException::new);
        UUID tenantId = user.tenantId();
        Product existing =
                products
                        .findByIdAndTenant(tenantId, productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));
        Product updated = existing.update(name, price, active);
        products.save(updated);
        return updated;
    }
}
