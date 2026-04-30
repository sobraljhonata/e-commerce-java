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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class ListProductsApiIntegrationTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;

  private String bearerToken() throws Exception {
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
    return mapper.readTree(loginBody).get("accessToken").asText();
  }

  @Test
  @DisplayName("GET /api/admin/products sem token retorna 401")
  void without_token_returns_401() throws Exception {
    mockMvc.perform(get("/api/admin/products")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("GET /api/admin/products com JWT retorna só produtos do tenant do token")
  void list_returns_products_for_token_tenant() throws Exception {
    String token = bearerToken();

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest("P1", new java.math.BigDecimal("1.00"), true))))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest("P2", new java.math.BigDecimal("2.00"), true))))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/api/admin/products").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(
            jsonPath("$[0].tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()))
        .andExpect(
            jsonPath("$[1].tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()));
  }
}
