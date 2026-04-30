package com.platform.iam.adapters.in.web;

import java.util.List;

/** Corpo JSON de erro do BC IAM (borda HTTP). */
public record IamApiErrorResponse(
    String code, String message, List<FieldViolation> fieldViolations) {

  public static IamApiErrorResponse invalidCredentials() {
    return new IamApiErrorResponse("INVALID_CREDENTIALS", "Invalid email or password", List.of());
  }

  public static IamApiErrorResponse validation(
      String message, List<FieldViolation> fieldViolations) {
    return new IamApiErrorResponse("VALIDATION_ERROR", message, fieldViolations);
  }

  /** Token autenticado mas com claims obrigatórias ausentes ou inválidas para o BC IAM. */
  public static IamApiErrorResponse invalidAuthenticatedToken() {
    return new IamApiErrorResponse(
        "INVALID_AUTHENTICATED_TOKEN",
        "Authentication token is missing required claims or has an invalid subject",
        List.of());
  }

  public record FieldViolation(String field, String message) {}
}
