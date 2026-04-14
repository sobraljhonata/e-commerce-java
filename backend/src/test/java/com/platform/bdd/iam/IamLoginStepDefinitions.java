package com.platform.bdd.iam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.iam.adapters.in.web.AuthController;
import com.platform.iam.adapters.in.web.IamExceptionHandler;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import com.platform.iam.adapters.out.security.BCryptPasswordVerifier;
import com.platform.iam.adapters.out.security.JwtAccessTokenIssuer;
import java.util.Optional;

import com.platform.iam.application.CurrentUserProvider;
import com.platform.iam.application.LoginUseCase;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

/**
 * BDD: login com stack real (use case + repositório em memória com seed + MockMvc).
 */
public class IamLoginStepDefinitions {

    private static final String JWT_SECRET = "bdd-jwt-secret-key-at-least-32-bytes-long!!";

    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private int lastStatus;
    private JsonNode lastJson;

    @Before
    public void reset() {
        var repo = new InMemoryAdminUserRepository();
        var loginUseCase =
            new LoginUseCase(
                repo, new BCryptPasswordVerifier(), new JwtAccessTokenIssuer(JWT_SECRET, 3600));
        CurrentUserProvider currentUser = Optional::empty;
        var controller = new AuthController(loginUseCase, currentUser);

        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc =
            MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new IamExceptionHandler())
                .setValidator(validator)
                .build();

        lastStatus = 0;
        lastJson = null;
    }

    @Dado("que existe um usuário administrativo válido")
    public void usuarioValido() {
        // garantido pelo seed em InMemoryAdminUserRepository
    }

    @Quando("o operador envia credenciais corretas")
    public void credenciaisCorretas() throws Exception {
        postLogin(InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);
    }

    @Quando("o operador envia senha incorreta")
    public void senhaIncorreta() throws Exception {
        postLogin(InMemoryAdminUserRepository.SEED_EMAIL, "wrong-password");
    }

    private void postLogin(String email, String password) throws Exception {
        ResultActions actions =
            mockMvc.perform(
                post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new LoginRequest(email, password))));
        capture(actions);
    }

    private void capture(ResultActions actions) throws Exception {
        var result = actions.andReturn();
        lastStatus = result.getResponse().getStatus();
        String body = result.getResponse().getContentAsString();
        lastJson = body.isBlank() ? null : mapper.readTree(body);
    }

    @Então("a API de login responde 200")
    public void responde200() {
        assertEquals(200, lastStatus);
    }

    @Então("o corpo contém um token de acesso")
    public void corpoComToken() {
        assertNotNull(lastJson);
        assertTrue(lastJson.has("accessToken"));
        assertTrue(lastJson.get("accessToken").asText().length() > 10);
        assertEquals("Bearer", lastJson.get("tokenType").asText());
    }

    @Então("a API de login responde 401")
    public void responde401() {
        assertEquals(401, lastStatus);
        assertNotNull(lastJson);
        assertEquals("INVALID_CREDENTIALS", lastJson.get("code").asText());
    }
}
