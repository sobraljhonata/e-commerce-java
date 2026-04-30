package com.platform.catalog.adapters.in.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Corpo de atualização explícita (name, price, active). {@code tenantId} não é aceito — vem do
 * contexto autenticado.
 */
public record UpdateProductRequest(
    @NotBlank String name,
    @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal price,
    @NotNull Boolean active) {}
