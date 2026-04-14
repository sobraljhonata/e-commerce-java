package com.platform.tenant;

public record TenantScopedRecord(
    String tenantId,
    String key,
    String value
) {}