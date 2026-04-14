package com.platform.iam.adapters.in.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import com.platform.iam.application.InvalidAuthenticatedTokenException;
import com.platform.iam.application.LoginUseCase;
import com.platform.iam.domain.InvalidCredentialsException;

@ExtendWith(MockitoExtension.class)
class AuthControllerWebTest {

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc =
            MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new IamExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void post_login_returns_200_with_token() throws Exception {
        when(loginUseCase.execute(anyString(), anyString()))
            .thenReturn(new LoginUseCase.LoginResult("jwt-token", "Bearer", 3600));

        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("a@b.c", "secret"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("jwt-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(3600));

        verify(loginUseCase).execute("a@b.c", "secret");
    }

    @Test
    void post_login_returns_401_when_invalid_credentials() throws Exception {
        when(loginUseCase.execute(anyString(), anyString())).thenThrow(new InvalidCredentialsException());

        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("a@b.c", "wrong"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void get_me_returns_200_with_authenticated_user() throws Exception {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID tenantId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        when(currentUserProvider.currentUser())
                .thenReturn(
                        Optional.of(
                                new AuthenticatedUser(
                                        id, tenantId, "op@platform.test", List.of("PLATFORM_ADMIN"))));

        mockMvc
                .perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(id.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId.toString()))
                .andExpect(jsonPath("$.email").value("op@platform.test"))
                .andExpect(jsonPath("$.roles[0]").value("PLATFORM_ADMIN"));
    }

    @Test
    void get_me_returns_401_when_no_current_user() throws Exception {
        when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void get_me_returns_401_when_authenticated_token_invalid() throws Exception {
        when(currentUserProvider.currentUser())
                .thenThrow(new InvalidAuthenticatedTokenException("JWT missing email claim"));

        mockMvc
                .perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_AUTHENTICATED_TOKEN"));
    }

    @Test
    void post_login_returns_400_when_body_invalid() throws Exception {
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("", "secret"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.fieldViolations[0].field").value("email"));
    }
}
