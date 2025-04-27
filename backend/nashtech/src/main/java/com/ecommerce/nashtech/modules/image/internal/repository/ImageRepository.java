package com.ecommerce.nashtech.modules.image.internal.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.image.model.Image;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ImageRepository extends R2dbcRepository<Image, Long> {

    Mono<Image> findById(Long id);

    Mono<Image> findByUuid(UUID uuid);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<Image> findByObjectKey(String objectKey);

    @Query("SELECT * FROM product_image WHERE product_id = :productId")
    Flux<Image> findAllByProductId(Long productId);

    @Query("""
                    SELECT *
                    FROM product_image AS pi
                    INNER JOIN product AS p ON pi.product_id = p.id
                    WHERE p.uuid = :productUuid
            """)
    Flux<Image> findAllByProductUuid(UUID productUuid);

    @Query("""
                    SELECT *
                    FROM product_image AS pi
                    INNER JOIN review AS r ON pi.review_id = r.id
                    WHERE r.uuid = :reviewUuid
            """)
    Flux<Image> findAllByReviewId(Long reviewId);

    @Query("""
                    SELECT *
                    FROM product_image AS pi
                    INNER JOIN review AS r ON pi.review_id = r.id
                    WHERE r.uuid = :reviewUuid
            """)
    Flux<Image> findAllByReviewUuid(UUID reviewUuid);
}
