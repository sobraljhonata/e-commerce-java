package com.platform.tenant;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TenantIsolationTest {

    @Test
    void tenant_a_should_not_see_data_from_tenant_b() {
        var repository = new InMemoryTenantScopedRecordRepository();

        repository.save(new TenantScopedRecord("tenant-a", "catalog-title", "Catalog A"));
        repository.save(new TenantScopedRecord("tenant-b", "catalog-title", "Catalog B"));

        Optional<TenantScopedRecord> resultForTenantA =
            repository.findByCurrentTenant("tenant-a", "catalog-title");

        Optional<TenantScopedRecord> resultForTenantB =
            repository.findByCurrentTenant("tenant-b", "catalog-title");

        assertTrue(resultForTenantA.isPresent());
        assertTrue(resultForTenantB.isPresent());

        assertEquals("Catalog A", resultForTenantA.get().value());
        assertEquals("Catalog B", resultForTenantB.get().value());

        assertNotEquals(resultForTenantA.get().value(), resultForTenantB.get().value());
    }

    @Test
    void tenant_b_should_not_access_record_from_tenant_a_when_querying_its_own_scope() {
        var repository = new InMemoryTenantScopedRecordRepository();

        repository.save(new TenantScopedRecord("tenant-a", "pricing-rule", "Rule A"));

        Optional<TenantScopedRecord> result =
            repository.findByCurrentTenant("tenant-b", "pricing-rule");

        assertTrue(result.isEmpty());
    }
}