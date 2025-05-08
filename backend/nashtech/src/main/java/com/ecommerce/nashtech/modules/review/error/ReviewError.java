package com.ecommerce.nashtech.modules.review.error;

import com.ecommerce.nashtech.shared.error.BaseError;

import java.util.UUID;

public sealed abstract class ReviewError extends BaseError permits
        ReviewError.ReviewNotFoundError,
        ReviewError.InvalidRatingError,
        ReviewError.DuplicateReviewError,
        ReviewError.UnauthorizedReviewModificationError,
        ReviewError.InappropriateContentError,
        ReviewError.ProductNotFoundForReviewError,
        ReviewError.CommentTooLongError,
        ReviewError.ReviewStatisticsError {

    protected ReviewError(String message, String code) {
        super(message, code);
    }

    public static final class ReviewNotFoundError extends ReviewError {
        private static final String CODE = "REVIEW_001";

        private ReviewNotFoundError(UUID uuid) {
            super("Review with UUID " + uuid + " not found", CODE);
        }

        private ReviewNotFoundError(Long id) {
            super("Review with ID " + id + " not found", CODE);
        }

        public static ReviewNotFoundError build(UUID uuid) {
            return new ReviewNotFoundError(uuid);
        }

        public static ReviewNotFoundError build(Long id) {
            return new ReviewNotFoundError(id);
        }
    }

    public static final class InvalidRatingError extends ReviewError {
        private static final String CODE = "REVIEW_002";

        private InvalidRatingError(Integer rating) {
            super("Rating value " + rating + " is invalid. Rating must be between 1 and 5", CODE);
        }

        public static InvalidRatingError build(Integer rating) {
            return new InvalidRatingError(rating);
        }
    }

    public static final class DuplicateReviewError extends ReviewError {
        private static final String CODE = "REVIEW_003";

        private DuplicateReviewError(UUID userId, UUID productId) {
            super("User " + userId + " has already reviewed product " + productId, CODE);
        }

        public static DuplicateReviewError build(UUID userId, UUID productId) {
            return new DuplicateReviewError(userId, productId);
        }
    }

    public static final class UnauthorizedReviewModificationError extends ReviewError {
        private static final String CODE = "REVIEW_004";

        private UnauthorizedReviewModificationError(UUID userId, UUID reviewId) {
            super("User " + userId + " is not authorized to modify review " + reviewId, CODE);
        }

        public static UnauthorizedReviewModificationError build(UUID userId, UUID reviewId) {
            return new UnauthorizedReviewModificationError(userId, reviewId);
        }
    }

    public static final class InappropriateContentError extends ReviewError {
        private static final String CODE = "REVIEW_005";

        private InappropriateContentError() {
            super("Review contains inappropriate content and cannot be posted", CODE);
        }

        public static InappropriateContentError build() {
            return new InappropriateContentError();
        }
    }

    public static final class ProductNotFoundForReviewError extends ReviewError {
        private static final String CODE = "REVIEW_006";

        private ProductNotFoundForReviewError(UUID productId) {
            super("Cannot create review for non-existent product " + productId, CODE);
        }

        public static ProductNotFoundForReviewError build(UUID productId) {
            return new ProductNotFoundForReviewError(productId);
        }
    }

    public static final class CommentTooLongError extends ReviewError {
        private static final String CODE = "REVIEW_007";

        private CommentTooLongError(int currentLength, int maxLength) {
            super("Review comment exceeds maximum length. Current: " + currentLength + ", Maximum: " + maxLength, CODE);
        }

        public static CommentTooLongError build(int currentLength, int maxLength) {
            return new CommentTooLongError(currentLength, maxLength);
        }
    }

    public static final class ReviewStatisticsError extends ReviewError {
        private static final String CODE = "REVIEW_008";

        private ReviewStatisticsError(UUID productId) {
            super("Failed to calculate review statistics for product " + productId, CODE);
        }

        public static ReviewStatisticsError build(UUID productId) {
            return new ReviewStatisticsError(productId);
        }
    }
}