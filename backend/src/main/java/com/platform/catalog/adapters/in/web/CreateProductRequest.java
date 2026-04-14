package com.platform.catalog.adapters.in.web;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo de criação de produto. {@code tenantId} não é aceito — vem do contexto autenticado (JWT).
 */
public record CreateProductRequest(
        @NotBlank String name,
        @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal price,
        Boolean active) {}
