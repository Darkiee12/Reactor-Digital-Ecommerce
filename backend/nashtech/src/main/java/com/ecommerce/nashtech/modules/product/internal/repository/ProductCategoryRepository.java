package com.ecommerce.nashtech.modules.product.internal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.product.model.ProductCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductCategoryRepository extends R2dbcRepository<ProductCategory, Void> {

    Flux<ProductCategory> findByProductId(Long productId);

    Flux<ProductCategory> findByCategoryId(Long categoryId);

    Mono<Void> deleteByProductIdAndCategoryId(Long productId, Long categoryId);
}
