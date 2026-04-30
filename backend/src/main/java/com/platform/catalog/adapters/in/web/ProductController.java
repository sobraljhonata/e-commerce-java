package com.platform.catalog.adapters.in.web;

import com.platform.catalog.application.CreateProductUseCase;
import com.platform.catalog.application.GetProductByIdUseCase;
import com.platform.catalog.application.ListProductsUseCase;
import com.platform.catalog.application.UpdateProductUseCase;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

  private final CreateProductUseCase createProductUseCase;
  private final GetProductByIdUseCase productByIdUseCase;
  private final ListProductsUseCase listProductsUseCase;
  private final UpdateProductUseCase updateProductUseCase;

  public ProductController(
      CreateProductUseCase createProduct,
      GetProductByIdUseCase productById,
      ListProductsUseCase listProducts,
      UpdateProductUseCase updateProduct) {
    this.createProductUseCase = createProduct;
    this.productByIdUseCase = productById;
    this.listProductsUseCase = listProducts;
    this.updateProductUseCase = updateProduct;
  }

  @GetMapping
  public List<ProductResponse> list() {
    return this.listProductsUseCase.execute().stream().map(ProductResponse::from).toList();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
    boolean active = request.active() != null ? request.active() : true;
    return ProductResponse.from(
        this.createProductUseCase.execute(request.name(), request.price(), active));
  }

  @GetMapping("/{id}")
  public ProductResponse getById(@PathVariable UUID id) {
    return ProductResponse.from(this.productByIdUseCase.execute(id));
  }

  @PatchMapping("/{id}")
  public ProductResponse update(
      @PathVariable UUID id, @Valid @RequestBody UpdateProductRequest request) {
    return ProductResponse.from(
        this.updateProductUseCase.execute(id, request.name(), request.price(), request.active()));
  }
}
