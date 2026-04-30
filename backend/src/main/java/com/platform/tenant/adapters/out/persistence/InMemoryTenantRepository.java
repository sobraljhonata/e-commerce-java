package com.platform.tenant.adapters.out.persistence;

import com.platform.tenant.application.TenantRepository;
import com.platform.tenant.domain.Tenant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTenantRepository implements TenantRepository {
  private final Map<UUID, Tenant> byId = new ConcurrentHashMap<>();
  private final Map<String, UUID> bySlug = new ConcurrentHashMap<>();

  @Override
  public Tenant save(Tenant tenant) {
    byId.put(tenant.id(), tenant);
    bySlug.put(tenant.slug(), tenant.id());
    return tenant;
  }

  @Override
  public Optional<Tenant> findById(UUID id) {
    return Optional.ofNullable(byId.get(id));
  }

  @Override
  public Optional<Tenant> findBySlug(String slug) {
    UUID id = bySlug.get(slug);
    if (id == null) return Optional.empty();
    return Optional.ofNullable(byId.get(id));
  }
}
