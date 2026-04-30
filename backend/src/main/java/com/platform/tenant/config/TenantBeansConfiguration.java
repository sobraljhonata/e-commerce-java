package com.platform.tenant.config;

import com.platform.tenant.adapters.out.persistence.InMemoryTenantRepository;
import com.platform.tenant.application.CreateTenantUseCase;
import com.platform.tenant.application.GetTenantByIdUseCase;
import com.platform.tenant.application.GetTenantBySlugUseCase;
import com.platform.tenant.application.TenantRepository;
import com.platform.tenant.application.UpdateTenantStatusUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantBeansConfiguration {

  @Bean
  TenantRepository tenantRepository() {
    return new InMemoryTenantRepository();
  }

  @Bean
  CreateTenantUseCase createTenantUseCase(TenantRepository tenants) {
    return new CreateTenantUseCase(tenants);
  }

  @Bean
  GetTenantByIdUseCase getTenantByIdUseCase(TenantRepository tenants) {
    return new GetTenantByIdUseCase(tenants);
  }

  @Bean
  GetTenantBySlugUseCase getTenantBySlugUseCase(TenantRepository tenants) {
    return new GetTenantBySlugUseCase(tenants);
  }

  @Bean
  UpdateTenantStatusUseCase updateTenantStatusUseCase(TenantRepository tenants) {
    return new UpdateTenantStatusUseCase(tenants);
  }
}
