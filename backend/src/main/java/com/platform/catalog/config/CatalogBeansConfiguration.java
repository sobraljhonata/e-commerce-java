package com.platform.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.platform.catalog.adapters.out.persistence.InMemoryCategoryRepository;
import com.platform.catalog.adapters.out.persistence.InMemoryProductRepository;
import com.platform.catalog.application.CategoryRepository;
import com.platform.catalog.application.CreateCategoryUseCase;
import com.platform.catalog.application.CreateProductUseCase;
import com.platform.catalog.application.GetCategoryByIdUseCase;
import com.platform.catalog.application.GetProductByIdUseCase;
import com.platform.catalog.application.ListProductsUseCase;
import com.platform.catalog.application.ProductRepository;
import com.platform.catalog.application.UpdateProductUseCase;
import com.platform.iam.application.CurrentUserProvider;

@Configuration
public class CatalogBeansConfiguration {

    @Bean
    ProductRepository productRepository() {
        return new InMemoryProductRepository();
    }

    @Bean
    CategoryRepository categoryRepository() {
        return new InMemoryCategoryRepository();
    }

    @Bean
    CreateProductUseCase createProductUseCase(ProductRepository products, CurrentUserProvider currentUserProvider) {
        return new CreateProductUseCase(products, currentUserProvider);
    }

    @Bean
    CreateCategoryUseCase createCategoryUseCase(
            CategoryRepository categories, CurrentUserProvider currentUserProvider) {
        return new CreateCategoryUseCase(categories, currentUserProvider);
    }

    @Bean
    GetProductByIdUseCase getProductByIdUseCase(ProductRepository products, CurrentUserProvider currentUserProvider) {
        return new GetProductByIdUseCase(products, currentUserProvider);
    }

    @Bean
    GetCategoryByIdUseCase getCategoryByIdUseCase(
            CategoryRepository categories, CurrentUserProvider currentUserProvider) {
        return new GetCategoryByIdUseCase(categories, currentUserProvider);
    }

    @Bean
    ListProductsUseCase listProductsUseCase(ProductRepository products, CurrentUserProvider currentUserProvider) {
        return new ListProductsUseCase(products, currentUserProvider);
    }

    @Bean
    UpdateProductUseCase updateProductUseCase(ProductRepository products, CurrentUserProvider currentUserProvider) {
        return new UpdateProductUseCase(products, currentUserProvider);
    }
}
