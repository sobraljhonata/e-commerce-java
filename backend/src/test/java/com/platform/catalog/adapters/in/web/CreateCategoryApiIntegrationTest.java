package com.platform.catalog.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.Wave1BackendApplication;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import com.platform.iam.domain.AdminRole;
import com.platform.iam.domain.AdminUser;

@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class CreateCategoryApiIntegrationTest {

    private static final String TENANT_B_EMAIL = "tenantb-admin@local.dev";
    private static final String TENANT_B_PASSWORD = "tenantb-secret";
    private static final UUID TENANT_B_ID = UUID.fromString("22222222-3333-4444-5555-666666666666");

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryAdminUserRepository adminUsers;

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
    @DisplayName("POST /api/admin/categories sem token retorna 401")
    void without_token_returns_401() throws Exception {
        mockMvc
                .perform(
                        post("/api/admin/categories")
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(new CreateCategoryRequest("Moda", true))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/admin/categories com JWT cria categoria no tenant do token")
    void with_jwt_creates_category_for_token_tenant() throws Exception {
        String token =
                bearerToken(
                        InMemoryAdminUserRepository.SEED_EMAIL,
                        InMemoryAdminUserRepository.SEED_PASSWORD);

        mockMvc
                .perform(
                        post("/api/admin/categories")
                                .header("Authorization", "Bearer " + token)
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(new CreateCategoryRequest("Moda", true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Moda"))
                .andExpect(jsonPath("$.tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /api/admin/categories ignora tenantId do payload e usa tenant do token")
    void post_ignores_tenant_id_in_payload() throws Exception {
        adminUsers.save(
                AdminUser.restore(
                        UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff"),
                        TENANT_B_ID,
                        TENANT_B_EMAIL,
                        new BCryptPasswordEncoder().encode(TENANT_B_PASSWORD),
                        true,
                        AdminRole.PLATFORM_ADMIN));
        String tenantBToken = bearerToken(TENANT_B_EMAIL, TENANT_B_PASSWORD);

        mockMvc
                .perform(
                        post("/api/admin/categories")
                                .header("Authorization", "Bearer " + tenantBToken)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        "{\"name\":\"Acessorios\",\"active\":true,"
                                                + "\"tenantId\":\""
                                                + InMemoryAdminUserRepository.SEED_TENANT_ID
                                                + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Acessorios"))
                .andExpect(jsonPath("$.tenantId").value(TENANT_B_ID.toString()));
    }
}
