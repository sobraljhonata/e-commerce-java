package com.platform.catalog.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Nenhum produto acessível para o tenant atual com o identificador informado (inexistente ou de
 * outro tenant — tratados igualmente para não vazar existência entre tenants).
 */
public final class ProductNotFoundException extends RuntimeException {

  private final UUID productId;

  public ProductNotFoundException(UUID productId) {
    super("Product not found: " + Objects.requireNonNull(productId, "productId"));
    this.productId = productId;
  }

  public UUID productId() {
    return productId;
  }
}
