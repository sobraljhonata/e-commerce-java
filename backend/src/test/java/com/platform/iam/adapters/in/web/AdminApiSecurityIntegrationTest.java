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
 * Incremento W1.2 — proteção de {@code /api/admin/**}: sem Bearer → 401; com JWT do login → acesso
 * (404 se recurso inexistente, não 401).
 */
@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class AdminApiSecurityIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/admin/** sem Authorization retorna 401")
    void admin_without_token_returns_401() throws Exception {
        mockMvc.perform(get("/api/admin/tenants/slug/qualquer")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login permanece público (200)")
    void login_stays_public() throws Exception {
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
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("GET /api/admin/** com JWT válido não retorna 401 (404 se tenant inexistente)")
    void admin_with_valid_jwt_is_not_401() throws Exception {
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
                .perform(
                        get("/api/admin/tenants/slug/inexistente-slug-xyz")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TENANT_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/admin/** com Bearer inválido retorna 401")
    void admin_with_invalid_jwt_returns_401() throws Exception {
        mockMvc
                .perform(
                        get("/api/admin/tenants/slug/x")
                                .header("Authorization", "Bearer not-a-real-jwt"))
                .andExpect(status().isUnauthorized());
    }
}
