package com.platform.tenant.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record CreateTenantRequest(@NotBlank String slug, @NotBlank String displayName) {}
