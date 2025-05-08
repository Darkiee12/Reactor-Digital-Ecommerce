package com.ecommerce.nashtech.modules.review.dto;

import java.util.UUID;

import com.ecommerce.nashtech.modules.review.model.Review;

import lombok.Builder;

@Builder
public record GetReviewDto(UUID uuid, String fullName, Integer rating, String comment, Long createdAt) {
    public static GetReviewDto from(Review review, String fullName) {
        return GetReviewDto.builder()
                .uuid(review.getUuid())
                .fullName(fullName)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
