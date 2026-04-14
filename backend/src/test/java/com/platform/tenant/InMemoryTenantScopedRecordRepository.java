package com.platform.tenant;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTenantScopedRecordRepository {

    private final Map<String, Map<String, TenantScopedRecord>> storage = new ConcurrentHashMap<>();

    public void save(TenantScopedRecord record) {
        storage
            .computeIfAbsent(record.tenantId(), ignored -> new ConcurrentHashMap<>())
            .put(record.key(), record);
    }

    public Optional<TenantScopedRecord> findByTenantIdAndKey(String tenantId, String key) {
        return Optional.ofNullable(
            storage.getOrDefault(tenantId, Map.of()).get(key)
        );
    }

    public Optional<TenantScopedRecord> findByCurrentTenant(String currentTenantId, String key) {
        return findByTenantIdAndKey(currentTenantId, key);
    }
}