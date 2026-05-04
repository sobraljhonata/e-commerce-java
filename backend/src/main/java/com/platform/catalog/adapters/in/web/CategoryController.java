package com.platform.catalog.adapters.in.web;

import com.platform.catalog.application.CreateCategoryUseCase;
import com.platform.catalog.application.GetCategoryByIdUseCase;
import com.platform.catalog.application.ListCategoriesUseCase;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

  private final CreateCategoryUseCase createCategoryUseCase;
  private final GetCategoryByIdUseCase getCategoryByIdUseCase;
  private final ListCategoriesUseCase listCategoriesUseCase;

  public CategoryController(
      CreateCategoryUseCase createCategoryUseCase,
      GetCategoryByIdUseCase getCategoryByIdUseCase,
      ListCategoriesUseCase listCategoriesUseCase) {
    this.createCategoryUseCase = createCategoryUseCase;
    this.getCategoryByIdUseCase = getCategoryByIdUseCase;
    this.listCategoriesUseCase = listCategoriesUseCase;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
    boolean active = request.active() != null ? request.active() : true;
    return CategoryResponse.from(createCategoryUseCase.execute(request.name(), active));
  }

  @GetMapping("/{id}")
  public CategoryResponse getById(@PathVariable UUID id) {
    return CategoryResponse.from(getCategoryByIdUseCase.execute(id));
  }

  @GetMapping
  public List<CategoryResponse> list() {
    return listCategoriesUseCase.execute().stream().map(CategoryResponse::from).toList();
  }
}
