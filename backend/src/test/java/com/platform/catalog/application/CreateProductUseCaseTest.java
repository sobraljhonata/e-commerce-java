package com.platform.catalog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

  @Mock private ProductRepository productRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private CreateProductUseCase useCase;

  @Test
  void persists_product_with_tenant_from_authenticated_user() {
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));

    Product result = useCase.execute("Notebook", new BigDecimal("1999.00"), true);

    assertNotNull(result.id());
    assertEquals(TENANT, result.tenantId());
    assertEquals("Notebook", result.name());

    ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository).save(captor.capture());
    assertEquals(TENANT, captor.getValue().tenantId());
  }

  @Test
  void fails_when_no_authenticated_context() {
    when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

    assertThrows(
        AuthenticatedContextRequiredException.class,
        () -> useCase.execute("X", BigDecimal.ONE, true));
    verify(productRepository, never()).save(any());
  }
}
