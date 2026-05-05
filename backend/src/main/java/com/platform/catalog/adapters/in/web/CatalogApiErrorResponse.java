package com.platform.catalog.adapters.in.web;

import java.util.List;

public record CatalogApiErrorResponse(
    String code, String message, List<FieldViolation> fieldViolations) {

  public static CatalogApiErrorResponse tenantContextRequired() {
    return new CatalogApiErrorResponse(
        "AUTHENTICATED_CONTEXT_REQUIRED",
        "Authenticated user context with tenant is required",
        List.of());
  }

  public static CatalogApiErrorResponse validation(
      String message, List<FieldViolation> fieldViolations) {
    return new CatalogApiErrorResponse("VALIDATION_ERROR", message, fieldViolations);
  }

  public static CatalogApiErrorResponse invalidRequest(String message) {
    return new CatalogApiErrorResponse("INVALID_REQUEST", message, List.of());
  }

  public static CatalogApiErrorResponse productNotFound(String message) {
    return new CatalogApiErrorResponse("PRODUCT_NOT_FOUND", message, List.of());
  }

  public static CatalogApiErrorResponse categoryNotFound(String message) {
    return new CatalogApiErrorResponse("CATEGORY_NOT_FOUND", message, List.of());
  }

  public static CatalogApiErrorResponse categoryInactive(String message) {
    return new CatalogApiErrorResponse("CATEGORY_INACTIVE", message, List.of());
  }

  public record FieldViolation(String field, String message) {}
}
