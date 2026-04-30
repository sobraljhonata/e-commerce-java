package com.platform.catalog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.platform.catalog.domain.Category;
import com.platform.catalog.domain.CategoryNotFoundException;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class GetCategoryByIdUseCaseTest {

    private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID USER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private GetCategoryByIdUseCase useCase;

    @Test
    void returns_category_when_same_tenant() {
        UUID categoryId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Category category = Category.restore(categoryId, TENANT, "Eletronicos", true);
        when(currentUserProvider.currentUser())
                .thenReturn(
                        Optional.of(
                                new AuthenticatedUser(
                                        USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
        when(categoryRepository.findByIdAndTenant(TENANT, categoryId)).thenReturn(Optional.of(category));

        assertEquals(category, useCase.execute(categoryId));
    }

    @Test
    void throws_not_found_when_repository_empty() {
        UUID categoryId = UUID.randomUUID();
        when(currentUserProvider.currentUser())
                .thenReturn(
                        Optional.of(
                                new AuthenticatedUser(
                                        USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
        when(categoryRepository.findByIdAndTenant(TENANT, categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> useCase.execute(categoryId));
    }

    @Test
    void fails_when_no_authenticated_context() {
        when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

        assertThrows(AuthenticatedContextRequiredException.class, () -> useCase.execute(UUID.randomUUID()));
        verifyNoInteractions(categoryRepository);
    }
}
