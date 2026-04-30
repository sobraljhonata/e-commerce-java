package com.platform.catalog.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.Wave1BackendApplication;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import com.platform.iam.domain.AdminRole;
import com.platform.iam.domain.AdminUser;

@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class GetCategoryByIdApiIntegrationTest {

    private static final String TENANT_B_EMAIL = "tenantb-reader@local.dev";
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
                                        .content(
                                                mapper.writeValueAsString(
                                                        new LoginRequest(
                                                                email,
                                                                password))))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return mapper.readTree(loginBody).get("accessToken").asText();
    }

    @Test
    @DisplayName("GET /api/admin/categories/{id} com JWT retorna 200 quando a categoria é do tenant")
    void get_returns_200_when_category_in_tenant() throws Exception {
        String token =
                bearerToken(
                        InMemoryAdminUserRepository.SEED_EMAIL,
                        InMemoryAdminUserRepository.SEED_PASSWORD);
        String createBody =
                mockMvc
                        .perform(
                                post("/api/admin/categories")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(APPLICATION_JSON)
                                        .content(mapper.writeValueAsString(new CreateCategoryRequest("Esportes", true))))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode created = mapper.readTree(createBody);
        String id = created.get("id").asText();

        mockMvc
                .perform(get("/api/admin/categories/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Esportes"))
                .andExpect(jsonPath("$.tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()));
    }

    @Test
    @DisplayName("GET /api/admin/categories/{id} retorna 404 para id inexistente")
    void get_returns_404_when_missing() throws Exception {
        String token =
                bearerToken(
                        InMemoryAdminUserRepository.SEED_EMAIL,
                        InMemoryAdminUserRepository.SEED_PASSWORD);
        UUID random = UUID.fromString("00000000-0000-0000-0000-000000000099");

        mockMvc
                .perform(get("/api/admin/categories/" + random).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/admin/categories/{id} retorna 404 quando categoria existe em outro tenant")
    void get_returns_404_when_category_belongs_to_another_tenant() throws Exception {
        String tenantAToken =
                bearerToken(
                        InMemoryAdminUserRepository.SEED_EMAIL,
                        InMemoryAdminUserRepository.SEED_PASSWORD);
        String createBody =
                mockMvc
                        .perform(
                                post("/api/admin/categories")
                                        .header("Authorization", "Bearer " + tenantAToken)
                                        .contentType(APPLICATION_JSON)
                                        .content(mapper.writeValueAsString(new CreateCategoryRequest("Livros", true))))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        String categoryId = mapper.readTree(createBody).get("id").asText();

        adminUsers.save(
                AdminUser.restore(
                        UUID.fromString("cccccccc-dddd-eeee-ffff-111111111111"),
                        TENANT_B_ID,
                        TENANT_B_EMAIL,
                        new BCryptPasswordEncoder().encode(TENANT_B_PASSWORD),
                        true,
                        AdminRole.PLATFORM_ADMIN));
        String tenantBToken = bearerToken(TENANT_B_EMAIL, TENANT_B_PASSWORD);

        mockMvc
                .perform(
                        get("/api/admin/categories/" + categoryId)
                                .header("Authorization", "Bearer " + tenantBToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/admin/categories/{id} sem token retorna 401")
    void get_without_token_returns_401() throws Exception {
        mockMvc.perform(get("/api/admin/categories/" + UUID.randomUUID())).andExpect(status().isUnauthorized());
    }
}
