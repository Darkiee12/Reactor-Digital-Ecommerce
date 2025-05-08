package com.ecommerce.nashtech.modules.review.service;

import com.ecommerce.nashtech.modules.review.dto.CreateReviewDto;
import com.ecommerce.nashtech.modules.review.dto.GetReviewDto;
import com.ecommerce.nashtech.modules.review.model.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IReviewService {
    Mono<Review> createReview(CreateReviewDto dto);

    Mono<GetReviewDto> findByUuid(UUID uuid);

    Mono<Review> updateReview(Review review);

    Mono<Boolean> existsByUuid(UUID uuid);

    Flux<GetReviewDto> findByProductUuid(UUID productId, Pageable pageable);

    Mono<Long> countByProductUuid(UUID productId);

    Flux<GetReviewDto> findByUserUuid(UUID userId);

    Mono<Double> getAverageRatingForProduct(UUID productId);

    Flux<GetReviewDto> findAll();

    Mono<Void> deleteByUuid(UUID id);
}
