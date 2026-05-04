package com.platform.catalog.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.Wave1BackendApplication;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import com.platform.iam.domain.AdminRole;
import com.platform.iam.domain.AdminUser;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class ListCategoriesApiIntegrationTest {

  private static final String TENANT_B_EMAIL = "tenantb-list@local.dev";
  private static final String TENANT_B_PASSWORD = "tenantb-secret";
  private static final UUID TENANT_B_ID = UUID.fromString("22222222-3333-4444-5555-666666666666");

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;

  @Autowired private InMemoryAdminUserRepository adminUsers;

  private String bearerToken(String email, String password) throws Exception {
    String loginBody =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new LoginRequest(email, password))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return mapper.readTree(loginBody).get("accessToken").asText();
  }

  @Test
  @DisplayName(
      "GET /api/admin/categories retorna 200 com array vazio quando tenant não tem categorias")
  void list_returns_empty_array_when_tenant_has_no_categories() throws Exception {
    String token =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);

    mockMvc
        .perform(get("/api/admin/categories").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("GET /api/admin/categories retorna apenas categorias do tenant autenticado")
  void list_returns_only_authenticated_tenant_categories() throws Exception {
    String tenantAToken =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);
    mockMvc
        .perform(
            post("/api/admin/categories")
                .header("Authorization", "Bearer " + tenantAToken)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(new CreateCategoryRequest("Moda", true))))
        .andExpect(status().isCreated());

    adminUsers.save(
        AdminUser.restore(
            UUID.fromString("dddddddd-eeee-ffff-1111-222222222222"),
            TENANT_B_ID,
            TENANT_B_EMAIL,
            new BCryptPasswordEncoder().encode(TENANT_B_PASSWORD),
            true,
            AdminRole.PLATFORM_ADMIN));
    String tenantBToken = bearerToken(TENANT_B_EMAIL, TENANT_B_PASSWORD);

    mockMvc
        .perform(get("/api/admin/categories").header("Authorization", "Bearer " + tenantBToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
