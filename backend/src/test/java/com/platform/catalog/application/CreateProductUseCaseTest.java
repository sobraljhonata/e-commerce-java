package com.platform.catalog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.platform.catalog.domain.Category;
import com.platform.catalog.domain.CategoryNotFoundException;
import com.platform.catalog.domain.Product;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

  private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final UUID USER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final UUID CATEGORY_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

  @Mock private ProductRepository productRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private CreateProductUseCase useCase;

  @Test
  void persists_product_with_tenant_from_authenticated_user() {
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));

    Product result = useCase.execute("Notebook", new BigDecimal("1999.00"), true, null);

    assertNotNull(result.id());
    assertEquals(TENANT, result.tenantId());
    assertEquals("Notebook", result.name());
    assertNull(result.categoryId());

    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());
    assertEquals(TENANT, captor.getValue().tenantId());
    verifyNoInteractions(categoryRepository);
  }

  @Test
  void with_category_id_calls_find_by_id_and_tenant() {
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(categoryRepository.findByIdAndTenant(TENANT, CATEGORY_ID))
        .thenReturn(Optional.of(Category.restore(CATEGORY_ID, TENANT, "Moda", true)));

    Product result = useCase.execute("Notebook", new BigDecimal("1999.00"), true, CATEGORY_ID);

    assertEquals(CATEGORY_ID, result.categoryId());
    verify(categoryRepository).findByIdAndTenant(TENANT, CATEGORY_ID);
    verify(productRepository).save(any(Product.class));
  }

  @Test
  void with_unknown_category_throws() {
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(categoryRepository.findByIdAndTenant(TENANT, CATEGORY_ID)).thenReturn(Optional.empty());

    assertThrows(
        CategoryNotFoundException.class,
        () -> useCase.execute("Notebook", new BigDecimal("1999.00"), true, CATEGORY_ID));
    verify(productRepository, never()).save(any());
  }

  @Test
  void fails_when_no_authenticated_context() {
    when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

    assertThrows(
        AuthenticatedContextRequiredException.class,
        () -> useCase.execute("X", BigDecimal.ONE, true, null));
    verify(productRepository, never()).save(any());
    verifyNoInteractions(categoryRepository);
  }
}
