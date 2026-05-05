package com.platform.catalog.adapters.in.web;

import com.platform.catalog.application.AuthenticatedContextRequiredException;
import com.platform.catalog.domain.CategoryInactiveException;
import com.platform.catalog.domain.CategoryNotFoundException;
import com.platform.catalog.domain.ProductNotFoundException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {ProductController.class, CategoryController.class})
public class CatalogExceptionHandler {

  @ExceptionHandler(AuthenticatedContextRequiredException.class)
  public ResponseEntity<CatalogApiErrorResponse> handleContextRequired(
      AuthenticatedContextRequiredException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(CatalogApiErrorResponse.tenantContextRequired());
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<CatalogApiErrorResponse> handleProductNotFound(
      ProductNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(CatalogApiErrorResponse.productNotFound(ex.getMessage()));
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<CatalogApiErrorResponse> handleCategoryNotFound(
      CategoryNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(CatalogApiErrorResponse.categoryNotFound(ex.getMessage()));
  }

  @ExceptionHandler(CategoryInactiveException.class)
  public ResponseEntity<CatalogApiErrorResponse> handleCategoryInactive(
      CategoryInactiveException ex) {
    return ResponseEntity.badRequest()
        .body(CatalogApiErrorResponse.categoryInactive(ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<CatalogApiErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex) {
    return ResponseEntity.badRequest()
        .body(CatalogApiErrorResponse.invalidRequest(ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CatalogApiErrorResponse> handleValidation(
      MethodArgumentNotValidException ex) {
    var violations =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fe ->
                    new CatalogApiErrorResponse.FieldViolation(
                        fe.getField(), fe.getDefaultMessage()))
            .collect(Collectors.toList());
    return ResponseEntity.badRequest()
        .body(CatalogApiErrorResponse.validation("Request validation failed", violations));
  }
}
