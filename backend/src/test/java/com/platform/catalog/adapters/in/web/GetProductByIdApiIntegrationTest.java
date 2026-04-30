package com.platform.catalog.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.Wave1BackendApplication;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class GetProductByIdApiIntegrationTest {

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
  @DisplayName("GET /api/admin/products/{id} com JWT retorna 200 quando o produto é do tenant")
  void get_returns_200_when_product_in_tenant() throws Exception {
    String token = bearerToken();
    String createBody =
        mockMvc
            .perform(
                post("/api/admin/products")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new CreateProductRequest(
                                "Livro", new java.math.BigDecimal("59.90"), true))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode created = mapper.readTree(createBody);
    String id = created.get("id").asText();

    mockMvc
        .perform(get("/api/admin/products/" + id).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value("Livro"))
        .andExpect(
            jsonPath("$.tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()));
  }

  @Test
  @DisplayName("GET /api/admin/products/{id} retorna 404 para id inexistente")
  void get_returns_404_when_missing() throws Exception {
    String token = bearerToken();
    UUID random = UUID.fromString("00000000-0000-0000-0000-000000000099");

    mockMvc
        .perform(get("/api/admin/products/" + random).header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
  }

  @Test
  @DisplayName("GET /api/admin/products/{id} sem token retorna 401")
  void get_without_token_returns_401() throws Exception {
    mockMvc
        .perform(get("/api/admin/products/" + UUID.randomUUID()))
        .andExpect(status().isUnauthorized());
  }
}
