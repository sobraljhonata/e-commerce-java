package com.platform.tenant.adapters.in.web;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.tenant.application.CreateTenantUseCase;
import com.platform.tenant.application.GetTenantByIdUseCase;
import com.platform.tenant.application.GetTenantBySlugUseCase;
import com.platform.tenant.application.UpdateTenantStatusUseCase;
import com.platform.tenant.domain.DuplicateTenantSlugException;
import com.platform.tenant.domain.Tenant;
import com.platform.tenant.domain.TenantNotFoundBySlugException;
import com.platform.tenant.domain.TenantNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class TenantControllerWebTest {

    @Mock
    private CreateTenantUseCase createTenantUseCase;

    @Mock
    private GetTenantByIdUseCase getTenantByIdUseCase;

    @Mock
    private GetTenantBySlugUseCase getTenantBySlugUseCase;

    @Mock
    private UpdateTenantStatusUseCase updateTenantStatusUseCase;

    @InjectMocks
    private TenantController tenantController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc =
            MockMvcBuilders.standaloneSetup(tenantController)
                .setControllerAdvice(new TenantExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void post_returns_201_when_create_succeeds() throws Exception {
        var id = UUID.randomUUID();
        var created =
            new Tenant(id, "acme", "Acme Corp", true);
        when(createTenantUseCase.execute("acme", "Acme Corp")).thenReturn(created);

        mockMvc
            .perform(
                post("/api/admin/tenants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateTenantRequest("acme", "Acme Corp"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.slug").value("acme"));

        verify(createTenantUseCase).execute("acme", "Acme Corp");
    }

    @Test
    void post_returns_409_when_slug_duplicate() throws Exception {
        when(createTenantUseCase.execute("acme", "Acme Corp"))
            .thenThrow(new DuplicateTenantSlugException("acme"));

        mockMvc
            .perform(
                post("/api/admin/tenants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateTenantRequest("acme", "Acme Corp"))))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_TENANT_SLUG"))
            .andExpect(jsonPath("$.slug").value("acme"));
    }

    @Test
    void post_returns_400_when_body_invalid() throws Exception {
        mockMvc
            .perform(
                post("/api/admin/tenants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateTenantRequest("", "Acme Corp"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.fieldViolations[0].field").value("slug"));
    }

    @Test
    void get_returns_200_when_tenant_exists() throws Exception {
        var id = UUID.randomUUID();
        var tenant = new Tenant(id, "acme", "Acme Corp", true);
        when(getTenantByIdUseCase.execute(id)).thenReturn(tenant);

        mockMvc
            .perform(get("/api/admin/tenants/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.slug").value("acme"))
            .andExpect(jsonPath("$.displayName").value("Acme Corp"))
            .andExpect(jsonPath("$.active").value(true));

        verify(getTenantByIdUseCase).execute(id);
    }

    @Test
    void get_returns_404_when_tenant_missing() throws Exception {
        var id = UUID.randomUUID();
        when(getTenantByIdUseCase.execute(id)).thenThrow(new TenantNotFoundException(id));

        mockMvc
            .perform(get("/api/admin/tenants/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TENANT_NOT_FOUND"))
            .andExpect(jsonPath("$.tenantId").value(id.toString()));

        verify(getTenantByIdUseCase).execute(id);
    }

    @Test
    void get_by_slug_returns_200_when_tenant_exists() throws Exception {
        var id = UUID.randomUUID();
        var tenant = new Tenant(id, "acme", "Acme Corp", true);
        when(getTenantBySlugUseCase.execute("Acme")).thenReturn(tenant);

        mockMvc
            .perform(get("/api/admin/tenants/slug/{slug}", "Acme"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.slug").value("acme"));

        verify(getTenantBySlugUseCase).execute("Acme");
    }

    @Test
    void get_by_slug_returns_404_when_missing() throws Exception {
        when(getTenantBySlugUseCase.execute("gone"))
            .thenThrow(new TenantNotFoundBySlugException("gone"));

        mockMvc
            .perform(get("/api/admin/tenants/slug/{slug}", "gone"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TENANT_NOT_FOUND"))
            .andExpect(jsonPath("$.slug").value("gone"));

        verify(getTenantBySlugUseCase).execute("gone");
    }

    @Test
    void patch_status_returns_200_with_updated_tenant() throws Exception {
        var id = UUID.randomUUID();
        var updated = new Tenant(id, "acme", "Acme Corp", false);
        when(updateTenantStatusUseCase.execute(id, false)).thenReturn(updated);

        mockMvc
            .perform(
                patch("/api/admin/tenants/{id}/status", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new UpdateTenantStatusRequest(false))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.slug").value("acme"))
            .andExpect(jsonPath("$.active").value(false));

        verify(updateTenantStatusUseCase).execute(id, false);
    }

    @Test
    void patch_status_returns_404_when_tenant_missing() throws Exception {
        var id = UUID.randomUUID();
        when(updateTenantStatusUseCase.execute(id, true)).thenThrow(new TenantNotFoundException(id));

        mockMvc
            .perform(
                patch("/api/admin/tenants/{id}/status", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new UpdateTenantStatusRequest(true))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TENANT_NOT_FOUND"))
            .andExpect(jsonPath("$.tenantId").value(id.toString()));

        verify(updateTenantStatusUseCase).execute(id, true);
    }
}
