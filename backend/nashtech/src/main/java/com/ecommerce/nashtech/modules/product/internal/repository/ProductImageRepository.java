package com.ecommerce.nashtech.modules.product.internal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.product.model.ProductImage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductImageRepository extends R2dbcRepository<ProductImage, Long> {

    Flux<ProductImage> findByProductId(Long productId);

    Flux<ProductImage> findByImageId(Long imageId);

    Mono<Void> deleteByProductIdAndImageId(Long productId, Long imageId);
}
