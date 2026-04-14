package com.platform.bdd.iam.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/**
 * BDD (incremento W1.2): rotas admin protegidas com stack Spring + Security + JWT.
 *
 * <p>Não usar {@code @Component}: o Cucumber Spring registra as glue classes explicitamente.
 */
public class AdminApiStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private String bearerToken;
    private int lastStatus;
    private JsonNode lastMeJson;

    @Before
    public void reset() {
        bearerToken = null;
        lastStatus = 0;
        lastMeJson = null;
    }

    @Dado("que o operador não está autenticado")
    public void naoAutenticado() {
        bearerToken = null;
    }

    @Dado("que o operador obteve um token válido")
    public void tokenValido() throws Exception {
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

    @Quando("solicita tenant por slug inexistente sem bearer token")
    public void getSemToken() throws Exception {
        lastStatus =
                mockMvc.perform(get("/api/admin/tenants/slug/inexistente")).andReturn().getResponse().getStatus();
    }

    @Quando("solicita tenant por slug inexistente com bearer token")
    public void getComToken() throws Exception {
        lastStatus =
                mockMvc
                        .perform(
                                get("/api/admin/tenants/slug/inexistente")
                                        .header("Authorization", "Bearer " + bearerToken))
                        .andReturn()
                        .getResponse()
                        .getStatus();
    }

    @Quando("o operador solicita o perfil autenticado com bearer token")
    public void getMeComToken() throws Exception {
        var res =
                mockMvc
                        .perform(
                                get("/api/auth/me").header("Authorization", "Bearer " + bearerToken))
                        .andReturn()
                        .getResponse();
        lastStatus = res.getStatus();
        String body = res.getContentAsString();
        lastMeJson = body.isBlank() ? null : mapper.readTree(body);
    }

    @Então("a API de perfil responde 200")
    public void me200() {
        assertEquals(200, lastStatus);
    }

    @Então("o corpo contém email, tenant e roles do operador")
    public void meBodyHasEmailTenantAndRoles() {
        assertNotNull(lastMeJson);
        assertTrue(lastMeJson.has("email"));
        assertTrue(lastMeJson.get("email").asText().contains("@"));
        assertEquals(
                InMemoryAdminUserRepository.SEED_TENANT_ID.toString(),
                lastMeJson.get("tenantId").asText());
        assertTrue(lastMeJson.has("roles"));
        assertTrue(lastMeJson.get("roles").isArray());
        assertTrue(lastMeJson.get("roles").size() > 0);
    }

    @Então("a API admin responde 401")
    public void api401() {
        assertEquals(401, lastStatus);
    }

    @Então("a API admin responde 404")
    public void api404() {
        assertEquals(404, lastStatus);
    }
}
