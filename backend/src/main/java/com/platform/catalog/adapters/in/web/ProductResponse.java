package com.platform.catalog.adapters.in.web;

import com.platform.catalog.domain.Product;
import java.math.BigDecimal;

public record ProductResponse(
    String id, String tenantId, String name, BigDecimal price, boolean active) {

  static ProductResponse from(Product product) {
    return new ProductResponse(
        product.id().toString(),
        product.tenantId().toString(),
        product.name(),
        product.price(),
        product.active());
  }
}
