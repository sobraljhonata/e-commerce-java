package com.platform.catalog.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.Wave1BackendApplication;
import com.platform.catalog.adapters.out.persistence.InMemoryCategoryRepository;
import com.platform.catalog.adapters.out.persistence.InMemoryProductRepository;
import com.platform.iam.adapters.in.web.LoginRequest;
import com.platform.iam.adapters.out.persistence.InMemoryAdminUserRepository;
import com.platform.iam.domain.AdminRole;
import com.platform.iam.domain.AdminUser;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = Wave1BackendApplication.class)
@AutoConfigureMockMvc
class ListProductsApiIntegrationTest {

  private static final String TENANT_B_EMAIL = "tenantb-list-prod@local.dev";
  private static final String TENANT_B_PASSWORD = "tenantb-secret";
  private static final UUID TENANT_B_ID = UUID.fromString("22222222-3333-4444-5555-666666666666");

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;

  @Autowired private InMemoryProductRepository productRepository;

  @Autowired private InMemoryCategoryRepository categoryRepository;

  @Autowired private InMemoryAdminUserRepository adminUsers;

  @BeforeEach
  void clear() {
    productRepository.clear();
    categoryRepository.clear();
  }

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
  @DisplayName("GET /api/admin/products sem token retorna 401")
  void without_token_returns_401() throws Exception {
    mockMvc.perform(get("/api/admin/products")).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("GET /api/admin/products com JWT retorna só produtos do tenant do token")
  void list_returns_products_for_token_tenant() throws Exception {
    String token = bearerToken();

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "P1", new java.math.BigDecimal("1.00"), true, null))))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "P2", new java.math.BigDecimal("2.00"), true, null))))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/api/admin/products").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(
            jsonPath("$[0].tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()))
        .andExpect(
            jsonPath("$[1].tenantId").value(InMemoryAdminUserRepository.SEED_TENANT_ID.toString()));
  }

  @Test
  @DisplayName("GET /api/admin/products?categoryId= retorna só produtos dessa categoria no tenant")
  void list_by_category_returns_matching_products() throws Exception {
    String token = bearerToken();

    String catBody =
        mockMvc
            .perform(
                post("/api/admin/categories")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new CreateCategoryRequest("Moda", true))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String categoryId = mapper.readTree(catBody).get("id").asText();

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "InCat",
                            new java.math.BigDecimal("10.00"),
                            true,
                            UUID.fromString(categoryId)))))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "NoCat", new java.math.BigDecimal("5.00"), true, null))))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            get("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .param("categoryId", categoryId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("InCat"))
        .andExpect(jsonPath("$[0].categoryId").value(categoryId));
  }

  @Test
  @DisplayName("GET /api/admin/products?categoryId= UUID inexistente retorna 404")
  void list_by_unknown_category_returns_404() throws Exception {
    String token = bearerToken();
    UUID random = UUID.fromString("00000000-0000-0000-0000-000000000099");

    mockMvc
        .perform(
            get("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .param("categoryId", random.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName("GET /api/admin/products?categoryId= categoria de outro tenant retorna 404")
  void list_by_other_tenant_category_returns_404() throws Exception {
    String tokenA =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);

    adminUsers.save(
        AdminUser.restore(
            UUID.fromString("99999999-aaaa-bbbb-cccc-dddddddddddd"),
            TENANT_B_ID,
            TENANT_B_EMAIL,
            new BCryptPasswordEncoder().encode(TENANT_B_PASSWORD),
            true,
            AdminRole.PLATFORM_ADMIN));
    String tokenB = bearerToken(TENANT_B_EMAIL, TENANT_B_PASSWORD);

    String catBBody =
        mockMvc
            .perform(
                post("/api/admin/categories")
                    .header("Authorization", "Bearer " + tokenB)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new CreateCategoryRequest("B-only", true))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    UUID categoryBId = UUID.fromString(mapper.readTree(catBBody).get("id").asText());

    mockMvc
        .perform(
            get("/api/admin/products")
                .header("Authorization", "Bearer " + tokenA)
                .param("categoryId", categoryBId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName("GET /api/admin/products?categoryId= válido sem produtos retorna 200 []")
  void list_by_category_empty_returns_200() throws Exception {
    String token = bearerToken();

    String catBody =
        mockMvc
            .perform(
                post("/api/admin/categories")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new CreateCategoryRequest("Vazia", true))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String categoryId = mapper.readTree(catBody).get("id").asText();

    mockMvc
        .perform(
            get("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .param("categoryId", categoryId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
