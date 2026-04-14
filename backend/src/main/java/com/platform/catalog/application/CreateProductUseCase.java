package com.platform.catalog.application;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import com.platform.catalog.domain.Product;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;

public final class CreateProductUseCase {

    private final ProductRepository products;
    private final CurrentUserProvider currentUserProvider;

    public CreateProductUseCase(ProductRepository products, CurrentUserProvider currentUserProvider) {
        this.products = Objects.requireNonNull(products);
        this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
    }

    public Product execute(String name, BigDecimal price, boolean active) {
        AuthenticatedUser user =
                currentUserProvider
                        .currentUser()
                        .orElseThrow(AuthenticatedContextRequiredException::new);
        UUID tenantId = user.tenantId();
        Product product = Product.create(tenantId, name, price, active);
        products.save(product);
        return product;
    }
}
