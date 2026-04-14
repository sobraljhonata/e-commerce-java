package com.platform.tenant.adapters.in.web;

import java.util.List;

/**
 * JSON body for API errors (adapter layer only).
 */
public record ApiErrorResponse(
        String code, String message, List<FieldViolation> fieldViolations, String slug, String tenantId) {

    public static ApiErrorResponse validation(String message, List<FieldViolation> fieldViolations) {
        return new ApiErrorResponse("VALIDATION_ERROR", message, fieldViolations, null, null);
    }

    public static ApiErrorResponse simple(String code, String message) {
        return new ApiErrorResponse(code, message, List.of(), null, null);
    }

    public static ApiErrorResponse duplicateSlug(String message, String slug) {
        return new ApiErrorResponse("DUPLICATE_TENANT_SLUG", message, List.of(), slug, null);
    }

    public static ApiErrorResponse tenantNotFound(String message, String tenantId) {
        return new ApiErrorResponse("TENANT_NOT_FOUND", message, List.of(), null, tenantId);
    }

    /** Same {@code TENANT_NOT_FOUND} code; {@code slug} carries the canonical slug looked up. */
    public static ApiErrorResponse tenantNotFoundForSlug(String message, String canonicalSlug) {
        return new ApiErrorResponse("TENANT_NOT_FOUND", message, List.of(), canonicalSlug, null);
    }

    public record FieldViolation(String field, String message) {}
}
