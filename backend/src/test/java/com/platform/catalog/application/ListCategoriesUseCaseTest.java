package com.platform.catalog.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.platform.catalog.domain.Category;
import com.platform.iam.application.AuthenticatedUser;
import com.platform.iam.application.CurrentUserProvider;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListCategoriesUseCaseTest {

  private static final UUID TENANT = UUID.fromString("11111111-2222-3333-4444-555555555555");
  private static final UUID USER = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

  @Mock private CategoryRepository categoryRepository;

  @Mock private CurrentUserProvider currentUserProvider;

  @InjectMocks private ListCategoriesUseCase useCase;

  @Test
  void calls_find_all_by_tenant_with_authenticated_context_tenant() {
    when(currentUserProvider.currentUser())
        .thenReturn(
            Optional.of(
                new AuthenticatedUser(USER, TENANT, "op@test.dev", List.of("PLATFORM_ADMIN"))));
    List<Category> expected = List.of(Category.restore(UUID.randomUUID(), TENANT, "Moda", true));
    when(categoryRepository.findAllByTenant(TENANT)).thenReturn(expected);

    assertEquals(expected, useCase.execute());
    verify(categoryRepository).findAllByTenant(TENANT);
  }

  @Test
  void fails_when_no_authenticated_context() {
    when(currentUserProvider.currentUser()).thenReturn(Optional.empty());

    assertThrows(AuthenticatedContextRequiredException.class, () -> useCase.execute());
    verifyNoInteractions(categoryRepository);
  }
}
