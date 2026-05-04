package com.platform.catalog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
class GetProductByIdUseCaseTest {

  private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final UUID USER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

  @Mock private ProductRepository productRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private GetProductByIdUseCase useCase;

  @Test
  void returns_product_when_same_tenant() {
    UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    Product p = Product.restore(productId, TENANT, "Item", BigDecimal.TEN, true, null);
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findByIdAndTenant(TENANT, productId)).thenReturn(Optional.of(p));

    assertEquals(p, useCase.execute(productId));
  }

  @Test
  void throws_not_found_when_repository_empty() {
    UUID productId = UUID.randomUUID();
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    when(productRepository.findByIdAndTenant(TENANT, productId)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> useCase.execute(productId));
  }

  @Test
  void fails_when_no_authenticated_context() {
    when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

    assertThrows(
        AuthenticatedContextRequiredException.class, () -> useCase.execute(UUID.randomUUID()));
    verifyNoInteractions(productRepository);
  }
}
