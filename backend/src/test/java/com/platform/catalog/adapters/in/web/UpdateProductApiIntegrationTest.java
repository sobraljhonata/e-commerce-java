package com.platform.catalog.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.Wave1BackendApplication;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;

@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class UpdateProductApiIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("PATCH com JWT atualiza nome preço e ativo do produto do tenant")
    void patch_updates_product_in_tenant() throws Exception {
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
                                                                "Antes", new java.math.BigDecimal("10.00"), true))))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode created = mapper.readTree(createBody);
        String id = created.get("id").asText();

        mockMvc
                .perform(
                        patch("/api/admin/products/" + id)
                                .header("Authorization", "Bearer " + token)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(
                                                new UpdateProductRequest(
                                                        "Depois", new java.math.BigDecimal("20.00"), false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Depois"))
                .andExpect(jsonPath("$.price").value(20.0))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()));

        mockMvc
                .perform(get("/api/admin/products/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Depois"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("PATCH retorna 404 para id inexistente")
    void patch_returns_404_when_missing() throws Exception {
        String token = bearerToken();
        UUID random = UUID.fromString("00000000-0000-0000-0000-000000000099");

        mockMvc
                .perform(
                        patch("/api/admin/products/" + random)
                                .header("Authorization", "Bearer " + token)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(
                                                new UpdateProductRequest(
                                                        "X", new java.math.BigDecimal("1.00"), true))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @Test
    @DisplayName("PATCH sem token retorna 401")
    void patch_without_token_returns_401() throws Exception {
        mockMvc
                .perform(
                        patch("/api/admin/products/" + UUID.randomUUID())
                                .contentType(APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(
                                                new UpdateProductRequest(
                                                        "X", new java.math.BigDecimal("1.00"), true))))
                .andExpect(status().isUnauthorized());
    }
}
