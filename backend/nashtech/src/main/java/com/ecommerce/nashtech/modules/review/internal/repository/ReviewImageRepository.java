package com.ecommerce.nashtech.modules.review.internal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.nashtech.modules.review.model.ReviewImage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ReviewImageRepository extends R2dbcRepository<ReviewImage, Long> {

    Flux<ReviewImage> findByReviewId(Long reviewId);

    Flux<ReviewImage> findByImageId(Long imageId);

    Mono<Void> deleteByReviewIdAndImageId(Long reviewId, Long imageId);
}
