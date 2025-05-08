package com.ecommerce.nashtech.modules.review.dto;

import java.time.Instant;
import java.util.UUID;

import com.ecommerce.nashtech.modules.review.model.Review;

public record CreateReviewDto(
        UUID productId,
        UUID userId,
        int rating,
        String comment) {
    public Review toReview() {
        return Review.builder()
                .uuid(UUID.randomUUID())
                .createdAt(Instant.now().toEpochMilli())
                .productId(productId)
                .userId(userId)
                .rating(rating)
                .comment(comment)
                .build();
    }
}
