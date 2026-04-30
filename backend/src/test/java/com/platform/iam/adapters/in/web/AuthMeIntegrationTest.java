package com.platform.iam.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.Wave1BackendApplication;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Incremento W1.2 — GET /api/auth/me: sem Bearer → 401; com JWT do login → 200 e corpo com id,
 * email e roles.
 */
@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class AuthMeIntegrationTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /api/auth/me sem Authorization retorna 401")
  void me_without_token_returns_401() throws Exception {
    mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("GET /api/auth/me com JWT válido retorna usuário autenticado")
  void me_with_valid_jwt_returns_profile() throws Exception {
    String body =
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

    String token = mapper.readTree(body).get("accessToken").asText();

    mockMvc
        .perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(InMemoryAdminUserRepository.SEED_EMAIL))
        .andExpect(
            jsonPath("$.tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()))
        .andExpect(jsonPath("$.roles[0]").value("PLATFORM_ADMIN"))
        .andExpect(jsonPath("$.userId").isString());
  }
}
