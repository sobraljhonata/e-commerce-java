package com.platform.catalog.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Corpo de criação de produto. {@code tenantId} não é aceito — vem do contexto autenticado (JWT).
 * {@code categoryId} é opcional; quando informado, deve existir no tenant do token.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateProductRequest(
    @NotBlank String name,
    @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal price,
    Boolean active,
    UUID categoryId) {}
