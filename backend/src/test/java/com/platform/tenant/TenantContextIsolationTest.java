package com.platform.tenant;

import com.platform.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TenantContextIsolationTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void should_read_only_data_from_current_tenant_context() {
        var repository = new InMemoryTenantScopedRecordRepository();

        repository.save(new TenantScopedRecord("tenant-a", "store-name", "Store A"));
        repository.save(new TenantScopedRecord("tenant-b", "store-name", "Store B"));

        TenantContext.setTenantId("tenant-a");
        Optional<TenantScopedRecord> resultA =
            repository.findByCurrentTenant(TenantContext.getTenantId(), "store-name");

        TenantContext.setTenantId("tenant-b");
        Optional<TenantScopedRecord> resultB =
            repository.findByCurrentTenant(TenantContext.getTenantId(), "store-name");

        assertTrue(resultA.isPresent());
        assertTrue(resultB.isPresent());
        assertEquals("Store A", resultA.get().value());
        assertEquals("Store B", resultB.get().value());
    }
}