package com.platform.tenant.adapters.in.web;

import jakarta.validation.constraints.NotNull;

/** Body for PATCH .../status only — explicit status transition, not a generic patch. */
public record UpdateTenantStatusRequest(@NotNull Boolean active) {}
