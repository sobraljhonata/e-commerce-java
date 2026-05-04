package com.platform.catalog.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.Wave1BackendApplication;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** Catalog + IAM: {@code tenantId} no produto vem do JWT (seed), não do corpo da requisição. */
@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class CreateProductApiIntegrationTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("POST /api/admin/products sem token retorna 401")
  void without_token_returns_401() throws Exception {
    mockMvc
        .perform(
            post("/api/admin/products")
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest("P", java.math.BigDecimal.ONE, true, null))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("POST /api/admin/products com JWT cria produto no tenant do token")
  void with_jwt_creates_product_for_token_tenant() throws Exception {
    String loginBody =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new LoginRequest(
                                InMemoryAdminUserRepository.SEED_EMAIL,
                                InMemoryAdminUserRepository.SEED_PASSWORD))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = mapper.readTree(loginBody).get("accessToken").asText();

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "Camiseta básica", new java.math.BigDecimal("39.99"), true, null))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Camiseta básica"))
        .andExpect(
            jsonPath("$.tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()))
        .andExpect(jsonPath("$.active").value(true))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  @DisplayName(
      "POST /api/admin/products: tenantId no JSON é ignorado; tenant do produto é o do JWT")
  void tenant_id_in_json_body_does_not_override_jwt_tenant() throws Exception {
    String loginBody =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new LoginRequest(
                                InMemoryAdminUserRepository.SEED_EMAIL,
                                InMemoryAdminUserRepository.SEED_PASSWORD))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = mapper.readTree(loginBody).get("accessToken").asText();
    String otherTenant = "22222222-3333-4444-5555-666666666666";

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    "{\"name\":\"Tee\",\"price\":19.99,\"active\":true,\"tenantId\":\""
                        + otherTenant
                        + "\"}"))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()));
  }
}
