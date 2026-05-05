package com.platform.catalog.adapters.in.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.hamcrest.Matchers;
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
class ProductCategoryAssociationApiIntegrationTest {

  private static final String TENANT_B_EMAIL = "tenantb-prod-cat@local.dev";
  private static final String TENANT_B_PASSWORD = "tenantb-secret";
  private static final UUID TENANT_B_ID = UUID.fromString("22222222-3333-4444-5555-666666666666");

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;

  @Autowired private InMemoryAdminUserRepository adminUsers;

  @Autowired private InMemoryCategoryRepository categoryRepository;

  @Autowired private InMemoryProductRepository productRepository;

  @BeforeEach
  void clear() {
    categoryRepository.clear();
    productRepository.clear();
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
  @DisplayName("POST produto sem categoryId — 201 e categoryId nulo")
  void create_without_category() throws Exception {
    String token =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "Solo", new java.math.BigDecimal("5.00"), true, null))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Solo"))
        .andExpect(jsonPath("$.categoryId").value(Matchers.nullValue()));
  }

  @Test
  @DisplayName("POST produto com categoryId válido do mesmo tenant — 201")
  void create_with_valid_category_same_tenant() throws Exception {
    String token =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);

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
                            "Camiseta",
                            new java.math.BigDecimal("39.99"),
                            true,
                            UUID.fromString(categoryId)))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.categoryId").value(categoryId));
  }

  @Test
  @DisplayName("POST produto com categoryId de categoria inativa — 400 CATEGORY_INACTIVE")
  void create_with_inactive_category_returns_400() throws Exception {
    String token =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);

    String catBody =
        mockMvc
            .perform(
                post("/api/admin/categories")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new CreateCategoryRequest("Off", false))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    UUID inactiveCategoryId = UUID.fromString(mapper.readTree(catBody).get("id").asText());

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "X", new java.math.BigDecimal("1.00"), true, inactiveCategoryId))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("CATEGORY_INACTIVE"));
  }

  @Test
  @DisplayName("POST produto com categoryId inexistente — 404 CATEGORY_NOT_FOUND")
  void create_with_unknown_category_returns_404() throws Exception {
    String token =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);
    UUID randomCat = UUID.fromString("00000000-0000-0000-0000-000000000099");

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "X", new java.math.BigDecimal("1.00"), true, randomCat))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName("POST produto com categoryId de outro tenant — 404")
  void create_with_other_tenant_category_returns_404() throws Exception {
    String tokenA =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);
    mockMvc
        .perform(
            post("/api/admin/categories")
                .header("Authorization", "Bearer " + tokenA)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(new CreateCategoryRequest("A-only", true))))
        .andExpect(status().isCreated());

    adminUsers.save(
        AdminUser.restore(
            UUID.fromString("ffffffff-1111-2222-3333-444444444444"),
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
                    .content(mapper.writeValueAsString(new CreateCategoryRequest("B-cat", true))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    UUID categoryBId = UUID.fromString(mapper.readTree(catBBody).get("id").asText());

    mockMvc
        .perform(
            post("/api/admin/products")
                .header("Authorization", "Bearer " + tokenA)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new CreateProductRequest(
                            "Leak", new java.math.BigDecimal("9.99"), true, categoryBId))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName("PATCH produto com categoryId válido do mesmo tenant — 200")
  void update_with_valid_category_same_tenant() throws Exception {
    String token =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);

    String catJson =
        mockMvc
            .perform(
                post("/api/admin/categories")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new CreateCategoryRequest("Cat1", true))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    UUID cat1 = UUID.fromString(mapper.readTree(catJson).get("id").asText());

    String cat2Json =
        mockMvc
            .perform(
                post("/api/admin/categories")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new CreateCategoryRequest("Cat2", true))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    UUID cat2 = UUID.fromString(mapper.readTree(cat2Json).get("id").asText());

    String productBody =
        mockMvc
            .perform(
                post("/api/admin/products")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new CreateProductRequest(
                                "P", new java.math.BigDecimal("10.00"), true, cat1))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String productId = mapper.readTree(productBody).get("id").asText();

    mockMvc
        .perform(
            patch("/api/admin/products/" + productId)
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new UpdateProductRequest(
                            "P2", new java.math.BigDecimal("11.00"), true, cat2))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categoryId").value(cat2.toString()));
  }

  @Test
  @DisplayName("PATCH produto com categoryId inativa — 400 CATEGORY_INACTIVE")
  void update_with_inactive_category_returns_400() throws Exception {
    String token =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);

    String productBody =
        mockMvc
            .perform(
                post("/api/admin/products")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new CreateProductRequest(
                                "P", new java.math.BigDecimal("10.00"), true, null))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String productId = mapper.readTree(productBody).get("id").asText();

    String catBody =
        mockMvc
            .perform(
                post("/api/admin/categories")
                    .header("Authorization", "Bearer " + token)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new CreateCategoryRequest("Off", false))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    UUID inactiveCategoryId = UUID.fromString(mapper.readTree(catBody).get("id").asText());

    mockMvc
        .perform(
            patch("/api/admin/products/" + productId)
                .header("Authorization", "Bearer " + token)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new UpdateProductRequest(
                            "P2", new java.math.BigDecimal("11.00"), true, inactiveCategoryId))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("CATEGORY_INACTIVE"));
  }

  @Test
  @DisplayName("PATCH produto com categoryId de outro tenant — 404")
  void update_with_other_tenant_category_returns_404() throws Exception {
    String tokenA =
        bearerToken(
            InMemoryAdminUserRepository.SEED_EMAIL, InMemoryAdminUserRepository.SEED_PASSWORD);
    String productBody =
        mockMvc
            .perform(
                post("/api/admin/products")
                    .header("Authorization", "Bearer " + tokenA)
                    .contentType(APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            new CreateProductRequest(
                                "P", new java.math.BigDecimal("1.00"), true, null))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String productId = mapper.readTree(productBody).get("id").asText();

    adminUsers.save(
        AdminUser.restore(
            UUID.fromString("eeeeeeee-1111-2222-3333-555555555555"),
            TENANT_B_ID,
            "tenantb-patch-cat@local.dev",
            new BCryptPasswordEncoder().encode(TENANT_B_PASSWORD),
            true,
            AdminRole.PLATFORM_ADMIN));
    String tokenB = bearerToken("tenantb-patch-cat@local.dev", TENANT_B_PASSWORD);

    String catBJson =
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
    UUID catB = UUID.fromString(mapper.readTree(catBJson).get("id").asText());

    mockMvc
        .perform(
            patch("/api/admin/products/" + productId)
                .header("Authorization", "Bearer " + tokenA)
                .contentType(APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        new UpdateProductRequest(
                            "P2", new java.math.BigDecimal("2.00"), true, catB))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
  }
}
