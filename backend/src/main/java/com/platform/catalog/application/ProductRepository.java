package com.platform.catalog.application;

import com.platform.catalog.domain.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

  void save(Product product);

  /**
   * Produto no escopo do tenant. Ausente se não existir ou se o id existir em outro tenant
   * (isolamento).
   */
  Optional<Product> findByIdAndTenant(UUID tenantId, UUID productId);

  /** Todos os produtos do tenant (ordem definida pelo adaptador de persistência). */
  List<Product> findAllByTenant(UUID tenantId);

  /**
   * Produtos do tenant com {@link Product#categoryId()} igual a {@code categoryId}. Categorias sem
   * produto associado resultam em lista vazia.
   */
  List<Product> findAllByTenantAndCategoryId(UUID tenantId, UUID categoryId);
}
