package com.platform.catalog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.platform.catalog.domain.Product;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class ListProductsUseCaseTest {

    private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID USER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private ListProductsUseCase useCase;

    @Test
    void returns_only_products_for_tenant() {
        Product a = Product.restore(UUID.randomUUID(), TENANT, "A", BigDecimal.ONE, true);
        Product b = Product.restore(UUID.randomUUID(), TENANT, "B", new BigDecimal("2.00"), true);
        when(currentUserProvider.currentUser())
                .thenReturn(
                        Optional.of(
                                new AuthenticatedUser(
                                        USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
        when(productRepository.findAllByTenant(TENANT)).thenReturn(List.of(a, b));

        List<Product> result = useCase.execute();

        assertEquals(2, result.size());
        assertTrue(result.contains(a));
        assertTrue(result.contains(b));
    }

    @Test
    void fails_when_no_authenticated_context() {
        when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

        assertThrows(AuthenticatedContextRequiredException.class, () -> useCase.execute());
        verifyNoInteractions(productRepository);
    }
}
