package com.platform.tenant.adapters.in.web;

import com.platform.tenant.domain.DuplicateTenantSlugException;
import com.platform.tenant.domain.TenantNotFoundBySlugException;
import com.platform.tenant.domain.TenantNotFoundException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = TenantController.class)
public class TenantExceptionHandler {

    @ExceptionHandler(DuplicateTenantSlugException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateSlug(DuplicateTenantSlugException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiErrorResponse.duplicateSlug(ex.getMessage(), ex.slug()));
    }

    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTenantNotFound(TenantNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.tenantNotFound(ex.getMessage(), ex.tenantId().toString()));
    }

    @ExceptionHandler(TenantNotFoundBySlugException.class)
    public ResponseEntity<ApiErrorResponse> handleTenantNotFoundBySlug(TenantNotFoundBySlugException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.tenantNotFoundForSlug(ex.getMessage(), ex.slug()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
            .body(ApiErrorResponse.simple("INVALID_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var violations =
            ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiErrorResponse.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        String message = "Request validation failed";
        return ResponseEntity.badRequest()
            .body(ApiErrorResponse.validation(message, violations));
    }
}
