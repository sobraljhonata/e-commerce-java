package com.platform.tenant.adapters.in.web;

import com.platform.tenant.application.CreateTenantUseCase;
import com.platform.tenant.application.GetTenantByIdUseCase;
import com.platform.tenant.application.GetTenantBySlugUseCase;
import com.platform.tenant.application.UpdateTenantStatusUseCase;
import com.platform.tenant.domain.Tenant;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tenants")
public class TenantController {
  private final CreateTenantUseCase createTenantUseCase;
  private final GetTenantByIdUseCase getTenantByIdUseCase;
  private final GetTenantBySlugUseCase getTenantBySlugUseCase;
  private final UpdateTenantStatusUseCase updateTenantStatusUseCase;

  public TenantController(
      CreateTenantUseCase createTenantUseCase,
      GetTenantByIdUseCase getTenantByIdUseCase,
      GetTenantBySlugUseCase getTenantBySlugUseCase,
      UpdateTenantStatusUseCase updateTenantStatusUseCase) {
    this.createTenantUseCase = createTenantUseCase;
    this.getTenantByIdUseCase = getTenantByIdUseCase;
    this.getTenantBySlugUseCase = getTenantBySlugUseCase;
    this.updateTenantStatusUseCase = updateTenantStatusUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TenantResponse create(@Valid @RequestBody CreateTenantRequest request) {
    Tenant tenant = createTenantUseCase.execute(request.slug(), request.displayName());
    return toResponse(tenant);
  }

  @GetMapping("/slug/{slug}")
  public TenantResponse getBySlug(@PathVariable String slug) {
    return toResponse(getTenantBySlugUseCase.execute(slug));
  }

  @PatchMapping("/{id}/status")
  public TenantResponse updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateTenantStatusRequest request) {
    Tenant tenant = updateTenantStatusUseCase.execute(id, request.active());
    return toResponse(tenant);
  }

  @GetMapping("/{id}")
  public TenantResponse getById(@PathVariable UUID id) {
    return toResponse(getTenantByIdUseCase.execute(id));
  }

  private static TenantResponse toResponse(Tenant tenant) {
    return new TenantResponse(
        tenant.id().toString(), tenant.slug(), tenant.displayName(), tenant.active());
  }
}
