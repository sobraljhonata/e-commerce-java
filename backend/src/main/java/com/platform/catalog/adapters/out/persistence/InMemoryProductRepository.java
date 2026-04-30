package com.platform.catalog.adapters.out.persistence;

import com.platform.catalog.application.ProductRepository;
import com.platform.catalog.domain.Product;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryProductRepository implements ProductRepository {

  private final Map<UUID, Product> byId = new ConcurrentHashMap<>();

  /** Clears all stored products (e.g. between integration/BDD scenarios). */
  public void clear() {
    byId.clear();
  }

  @Override
  public void save(Product product) {
    byId.put(product.id(), product);
  }

  @Override
  public Optional<Product> findByIdAndTenant(UUID tenantId, UUID productId) {
    Product found = byId.get(productId);
    if (found == null) {
      return Optional.empty();
    }
    if (!found.tenantId().equals(tenantId)) {
      return Optional.empty();
    }
    return Optional.of(found);
  }

  @Override
  public List<Product> findAllByTenant(UUID tenantId) {
    return byId.values().stream()
        .filter(p -> p.tenantId().equals(tenantId))
        .sorted(Comparator.comparing(Product::id))
        .toList();
  }
}
