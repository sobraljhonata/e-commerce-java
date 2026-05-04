package com.platform.catalog.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Corpo de atualização explícita (name, price, active). {@code tenantId} não é aceito — vem do
 * contexto autenticado. {@code categoryId} opcional: ausente ou {@code null} mantém a associação
 * atual; valor não nulo substitui e é validado no tenant do token.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateProductRequest(
    @NotBlank String name,
    @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal price,
    @NotNull Boolean active,
    UUID categoryId) {}
