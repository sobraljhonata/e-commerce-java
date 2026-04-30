package com.platform.catalog.adapters.in.web;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.catalog.application.CreateProductUseCase;
import com.platform.catalog.application.GetProductByIdUseCase;
import com.platform.catalog.application.ListProductsUseCase;
import com.platform.catalog.application.UpdateProductUseCase;
import com.platform.catalog.domain.Product;
import com.platform.catalog.domain.ProductNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
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

@ExtendWith(MockitoExtension.class)
class ProductControllerWebTest {

  private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");

  @Mock private CreateProductUseCase createProductUseCase;

  @Mock private GetProductByIdUseCase getProductByIdUseCase;

  @Mock private ListProductsUseCase listProductsUseCase;

  @Mock private UpdateProductUseCase updateProductUseCase;

  @InjectMocks private ProductController productController;

  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    var validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    mockMvc =
        MockMvcBuilders.standaloneSetup(productController)
            .setControllerAdvice(new CatalogExceptionHandler())
            .setValidator(validator)
            .build();
  }

  @Test
  void post_creates_product_returns_201() throws Exception {
    UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product created = Product.restore(id, TENANT, "Camiseta", new BigDecimal("49.90"), true);
    when(createProductUseCase.execute(eq("Camiseta"), eq(new BigDecimal("49.90")), eq(true)))
        .thenReturn(created);

    mockMvc
        .perform(
            post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new CreateProductRequest("Camiseta", new BigDecimal("49.90"), true))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.tenantId").value(TENANT.toString()))
        .andExpect(jsonPath("$.name").value("Camiseta"))
        .andExpect(jsonPath("$.price").value(49.9))
        .andExpect(jsonPath("$.active").value(true));

    verify(createProductUseCase).execute("Camiseta", new BigDecimal("49.90"), true);
  }

  @Test
  void post_defaults_active_true_when_omitted() throws Exception {
    UUID id = UUID.randomUUID();
    Product created = Product.restore(id, TENANT, "X", BigDecimal.ONE, true);
    when(createProductUseCase.execute(eq("X"), eq(BigDecimal.ONE), anyBoolean()))
        .thenReturn(created);

    mockMvc
        .perform(
            post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"X\",\"price\":1}"))
        .andExpect(status().isCreated());

    verify(createProductUseCase).execute("X", BigDecimal.ONE, true);
  }

  @Test
  void post_returns_400_when_validation_fails() throws Exception {
    mockMvc
        .perform(
            post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"price\":1}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void get_list_returns_200_with_array() throws Exception {
    UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product p = Product.restore(id, TENANT, "Camiseta", new BigDecimal("49.90"), true);
    when(listProductsUseCase.execute()).thenReturn(List.of(p));

    mockMvc
        .perform(get("/api/admin/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].id").value(id.toString()))
        .andExpect(jsonPath("$[0].name").value("Camiseta"));
  }

  @Test
  void get_by_id_returns_200() throws Exception {
    UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product p = Product.restore(id, TENANT, "Camiseta", new BigDecimal("49.90"), true);
    when(getProductByIdUseCase.execute(id)).thenReturn(p);

    mockMvc
        .perform(get("/api/admin/products/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Camiseta"));
  }

  @Test
  void get_by_id_returns_404_when_not_found() throws Exception {
    UUID id = UUID.randomUUID();
    when(getProductByIdUseCase.execute(id)).thenThrow(new ProductNotFoundException(id));

    mockMvc
        .perform(get("/api/admin/products/" + id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
  }

  @Test
  void patch_updates_product_returns_200() throws Exception {
    UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product updated = Product.restore(id, TENANT, "Novo", new BigDecimal("12.50"), false);
    when(updateProductUseCase.execute(eq(id), eq("Novo"), eq(new BigDecimal("12.50")), eq(false)))
        .thenReturn(updated);

    mockMvc
        .perform(
            patch("/api/admin/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new UpdateProductRequest("Novo", new BigDecimal("12.50"), false))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Novo"))
        .andExpect(jsonPath("$.price").value(12.5))
        .andExpect(jsonPath("$.active").value(false));

    verify(updateProductUseCase).execute(id, "Novo", new BigDecimal("12.50"), false);
  }

  @Test
  void patch_returns_404_when_not_found() throws Exception {
    UUID id = UUID.randomUUID();
    when(updateProductUseCase.execute(eq(id), eq("X"), eq(BigDecimal.ONE), eq(true)))
        .thenThrow(new ProductNotFoundException(id));

    mockMvc
        .perform(
            patch("/api/admin/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new UpdateProductRequest("X", BigDecimal.ONE, true))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
  }
}
