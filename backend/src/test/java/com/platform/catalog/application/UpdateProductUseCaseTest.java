package com.platform.catalog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.platform.catalog.domain.Category;
import com.platform.catalog.domain.CategoryInactiveException;
import com.platform.catalog.domain.CategoryNotFoundException;
import com.platform.catalog.domain.Product;
import com.platform.catalog.domain.ProductNotFoundException;
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
class UpdateProductUseCaseTest {

  private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final UUID USER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
  private static final UUID CAT = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
  @Mock private ProductRepository productRepository;

  @Mock private CategoryRepository categoryRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private UpdateProductUseCase useCase;

  @Test
  void updates_and_persists_when_same_tenant() {
    UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product existing = Product.restore(productId, TENANT, "Old", BigDecimal.ONE, true, null);
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findByIdAndTenant(TENANT, productId)).thenReturn(Optional.of(existing));

    Product result = useCase.execute(productId, "New", new BigDecimal("9.99"), false, null);

    assertEquals(productId, result.id());
    assertEquals(TENANT, result.tenantId());
    assertEquals("New", result.name());
    assertEquals(0, new BigDecimal("9.99").compareTo(result.price()));
    assertEquals(false, result.active());
    assertNull(result.categoryId());

    verify(productRepository).save(result);
    verifyNoInteractions(categoryRepository);
  }

  @Test
  void with_category_in_request_validates_and_persists() {
    UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product existing = Product.restore(productId, TENANT, "Old", BigDecimal.ONE, true, null);
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findByIdAndTenant(TENANT, productId)).thenReturn(Optional.of(existing));
    when(categoryRepository.findByIdAndTenant(TENANT, CAT))
        .thenReturn(Optional.of(Category.restore(CAT, TENANT, "X", true)));

    Product result = useCase.execute(productId, "New", new BigDecimal("9.99"), false, CAT);

    assertEquals(CAT, result.categoryId());
    verify(categoryRepository).findByIdAndTenant(TENANT, CAT);
    verify(productRepository).save(result);
  }

  @Test
  void with_category_from_other_tenant_returns_not_found() {
    UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product existing = Product.restore(productId, TENANT, "Old", BigDecimal.ONE, true, null);
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findByIdAndTenant(TENANT, productId)).thenReturn(Optional.of(existing));
    when(categoryRepository.findByIdAndTenant(TENANT, CAT)).thenReturn(Optional.empty());

    assertThrows(
        CategoryNotFoundException.class,
        () -> useCase.execute(productId, "New", new BigDecimal("9.99"), false, CAT));
  }

  @Test
  void with_inactive_category_returns_category_inactive() {
    UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product existing = Product.restore(productId, TENANT, "Old", BigDecimal.ONE, true, null);
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findByIdAndTenant(TENANT, productId)).thenReturn(Optional.of(existing));
    when(categoryRepository.findByIdAndTenant(TENANT, CAT))
        .thenReturn(Optional.of(Category.restore(CAT, TENANT, "X", false)));

    assertThrows(
        CategoryInactiveException.class,
        () -> useCase.execute(productId, "New", new BigDecimal("9.99"), false, CAT));
  }

  @Test
  void keeps_category_when_request_category_id_null_and_revalidates_existing() {
    UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product existing = Product.restore(productId, TENANT, "Old", BigDecimal.ONE, true, CAT);
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findByIdAndTenant(TENANT, productId)).thenReturn(Optional.of(existing));
    when(categoryRepository.findByIdAndTenant(TENANT, CAT))
        .thenReturn(Optional.of(Category.restore(CAT, TENANT, "X", true)));

    Product result = useCase.execute(productId, "New", new BigDecimal("9.99"), false, null);

    assertEquals(CAT, result.categoryId());
    verify(categoryRepository).findByIdAndTenant(TENANT, CAT);
    verify(productRepository).save(result);
  }

  @Test
  void throws_not_found_when_repository_empty() {
    UUID productId = UUID.randomUUID();
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findByIdAndTenant(TENANT, productId)).thenReturn(Optional.empty());

    assertThrows(
        ProductNotFoundException.class,
        () -> useCase.execute(productId, "X", BigDecimal.ONE, true, null));
  }

  @Test
  void fails_when_no_authenticated_context() {
    when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

    assertThrows(
        AuthenticatedContextRequiredException.class,
        () -> useCase.execute(UUID.randomUUID(), "X", BigDecimal.ONE, true, null));
    verifyNoInteractions(productRepository);
    verifyNoInteractions(categoryRepository);
  }
}
