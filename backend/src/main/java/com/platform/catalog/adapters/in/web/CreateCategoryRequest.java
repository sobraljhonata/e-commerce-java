package com.platform.catalog.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

/** Corpo de criação de categoria. tenantId vem do contexto autenticado. */
public record CreateCategoryRequest(@NotBlank String name, Boolean active) {}
