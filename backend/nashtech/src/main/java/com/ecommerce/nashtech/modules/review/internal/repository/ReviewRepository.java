package com.ecommerce.nashtech.modules.review.internal.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.review.model.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReviewRepository extends R2dbcRepository<Review, Long> {

    Mono<Review> findById(Long id);

    Mono<Review> findByUuid(UUID uuid);

    Flux<Review> findByProductId(UUID productId, Pageable pageable);

    Mono<Long> countByProductId(UUID productId);

    Flux<Review> findAllByProductId(UUID productId);

    Flux<Review> findByUserId(UUID userId);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByUuid(UUID uuid);

    Mono<Void> deleteByUuid(UUID id);
}
