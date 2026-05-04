package com.platform.catalog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

  private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final UUID USER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final UUID CAT = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

  @Mock private ProductRepository productRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private ListProductsUseCase useCase;

  @Test
  void returns_only_products_for_tenant() {
    Product a = Product.restore(UUID.randomUUID(), TENANT, "A", BigDecimal.ONE, true, null);
    Product b = Product.restore(UUID.randomUUID(), TENANT, "B", new BigDecimal("2.00"), true, null);
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findAllByTenant(TENANT)).thenReturn(List.of(a, b));

    List<Product> result = useCase.execute(null);

    assertEquals(2, result.size());
    assertTrue(result.contains(a));
    assertTrue(result.contains(b));
    verifyNoInteractions(categoryRepository);
  }

  @Test
  void with_category_validates_and_queries_by_category() {
    Product p = Product.restore(UUID.randomUUID(), TENANT, "InCat", BigDecimal.ONE, true, CAT);
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(categoryRepository.findByIdAndTenant(TENANT, CAT))
        .thenReturn(Optional.of(Category.restore(CAT, TENANT, "Moda", true)));
    when(productRepository.findAllByTenantAndCategoryId(TENANT, CAT)).thenReturn(List.of(p));

    List<Product> result = useCase.execute(CAT);

    assertEquals(1, result.size());
    assertEquals(p, result.get(0));
    verify(categoryRepository).findByIdAndTenant(TENANT, CAT);
    verify(productRepository).findAllByTenantAndCategoryId(TENANT, CAT);
  }

  @Test
  void with_unknown_category_throws() {
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(categoryRepository.findByIdAndTenant(TENANT, CAT)).thenReturn(Optional.empty());

    assertThrows(CategoryNotFoundException.class, () -> useCase.execute(CAT));
    verifyNoInteractions(productRepository);
  }

  @Test
  void fails_when_no_authenticated_context() {
    when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

    assertThrows(AuthenticatedContextRequiredException.class, () -> useCase.execute(null));
    verifyNoInteractions(productRepository);
    verifyNoInteractions(categoryRepository);
  }
}
