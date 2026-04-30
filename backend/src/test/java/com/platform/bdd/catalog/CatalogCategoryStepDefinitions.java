package com.platform.bdd.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.catalog.adapters.in.web.CreateCategoryRequest;
import com.platform.catalog.adapters.out.persistence.InMemoryCategoryRepository;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class CatalogCategoryStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryCategoryRepository categoryRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    private String bearerToken;
    private int lastStatus;
    private JsonNode lastJson;
    private String lastCategoryId;
    private int lastGetStatus;
    private JsonNode lastGetJson;

    @Before
    public void reset() {
        categoryRepository.clear();
        bearerToken = null;
        lastStatus = 0;
        lastJson = null;
        lastCategoryId = null;
        lastGetStatus = 0;
        lastGetJson = null;
    }

    @Dado("que o operador de categorias está autenticado como admin de plataforma")
    public void loginComoAdmin() throws Exception {
        var res =
                mockMvc
                        .perform(
                                post("/api/auth/login")
                                        .contentType(APPLICATION_JSON)
                                        .content(
                                                mapper.writeValueAsString(
                                                        new LoginRequest(
                                                                InMemoryAdminUserRepository.SEED_EMAIL,
                                                                InMemoryAdminUserRepository.SEED_PASSWORD))))
                        .andReturn()
                        .getResponse();
        JsonNode json = mapper.readTree(res.getContentAsString());
        bearerToken = json.get("accessToken").asText();
    }

    @Quando("cadastra uma categoria com nome {string}")
    public void cadastraCategoria(String nome) throws Exception {
        var res =
                mockMvc
                        .perform(
                                post("/api/admin/categories")
                                        .header("Authorization", "Bearer " + bearerToken)
                                        .contentType(APPLICATION_JSON)
                                        .content(mapper.writeValueAsString(new CreateCategoryRequest(nome, true))))
                        .andReturn()
                        .getResponse();
        lastStatus = res.getStatus();
        String body = res.getContentAsString();
        lastJson = body.isBlank() ? null : mapper.readTree(body);
    }

    @Então("a API de categorias responde 201")
    public void responde201() {
        assertEquals(201, lastStatus);
    }

    @Então("o tenant da categoria é o do usuário seed")
    public void tenantDaCategoriaIgualSeed() {
        assertNotNull(lastJson);
        assertEquals(
                InMemoryAdminUserRepository.SEED_TENANT_ID.toString(),
                lastJson.get("tenantId").asText());
    }

    @E("que uma categoria foi cadastrada com nome {string}")
    public void cadastraParaConsulta(String nome) throws Exception {
        var res =
                mockMvc
                        .perform(
                                post("/api/admin/categories")
                                        .header("Authorization", "Bearer " + bearerToken)
                                        .contentType(APPLICATION_JSON)
                                        .content(mapper.writeValueAsString(new CreateCategoryRequest(nome, true))))
                        .andReturn()
                        .getResponse();
        assertEquals(201, res.getStatus());
        JsonNode json = mapper.readTree(res.getContentAsString());
        lastCategoryId = json.get("id").asText();
    }

    @Quando("consulta essa categoria pelo id do cadastro")
    public void consultaPorId() throws Exception {
        var res =
                mockMvc
                        .perform(
                                get("/api/admin/categories/" + lastCategoryId)
                                        .header("Authorization", "Bearer " + bearerToken))
                        .andReturn()
                        .getResponse();
        lastGetStatus = res.getStatus();
        String body = res.getContentAsString();
        lastGetJson = body.isBlank() ? null : mapper.readTree(body);
    }

    @Então("a resposta da consulta de categoria é 200")
    public void consulta200() {
        assertEquals(200, lastGetStatus);
    }

    @Então("o nome da categoria retornada é {string}")
    public void nomeCategoriaConsulta(String nomeEsperado) {
        assertNotNull(lastGetJson);
        assertEquals(nomeEsperado, lastGetJson.get("name").asText());
    }
}
