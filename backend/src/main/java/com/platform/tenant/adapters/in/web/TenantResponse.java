package com.platform.tenant.adapters.in.web;

public record TenantResponse(
    String id,
    String slug,
    String displayName,
    boolean active
) {}
