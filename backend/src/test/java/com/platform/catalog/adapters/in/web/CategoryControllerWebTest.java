package com.platform.catalog.adapters.in.web;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.catalog.application.CreateCategoryUseCase;
import com.platform.catalog.application.GetCategoryByIdUseCase;
import com.platform.catalog.domain.Category;
import com.platform.catalog.domain.CategoryNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoryControllerWebTest {

    private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @Mock
    private CreateCategoryUseCase createCategoryUseCase;

    @Mock
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc =
                MockMvcBuilders.standaloneSetup(categoryController)
                        .setControllerAdvice(new CatalogExceptionHandler())
                        .setValidator(validator)
                        .build();
    }

    @Test
    void post_creates_category_returns_201() throws Exception {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Category created = Category.restore(id, TENANT, "Roupas", true);
        when(createCategoryUseCase.execute(eq("Roupas"), eq(true))).thenReturn(created);

        mockMvc
                .perform(
                        post("/api/admin/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CreateCategoryRequest("Roupas", true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.tenantId").value(TENANT.toString()))
                .andExpect(jsonPath("$.name").value("Roupas"))
                .andExpect(jsonPath("$.active").value(true));

        verify(createCategoryUseCase).execute("Roupas", true);
    }

    @Test
    void post_defaults_active_true_when_omitted() throws Exception {
        UUID id = UUID.randomUUID();
        Category created = Category.restore(id, TENANT, "X", true);
        when(createCategoryUseCase.execute(eq("X"), anyBoolean())).thenReturn(created);

        mockMvc
                .perform(
                        post("/api/admin/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"X\"}"))
                .andExpect(status().isCreated());

        verify(createCategoryUseCase).execute("X", true);
    }

    @Test
    void post_returns_400_when_validation_fails() throws Exception {
        mockMvc
                .perform(
                        post("/api/admin/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void get_by_id_returns_200() throws Exception {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Category category = Category.restore(id, TENANT, "Roupas", true);
        when(getCategoryByIdUseCase.execute(id)).thenReturn(category);

        mockMvc
                .perform(get("/api/admin/categories/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Roupas"));
    }

    @Test
    void get_by_id_returns_404_when_not_found() throws Exception {
        UUID id = UUID.randomUUID();
        when(getCategoryByIdUseCase.execute(id)).thenThrow(new CategoryNotFoundException(id));

        mockMvc
                .perform(get("/api/admin/categories/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }
}
