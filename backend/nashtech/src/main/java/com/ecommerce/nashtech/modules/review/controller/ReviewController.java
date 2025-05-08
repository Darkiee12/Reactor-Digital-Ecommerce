package com.ecommerce.nashtech.modules.review.controller;

import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import com.ecommerce.nashtech.modules.review.dto.CreateReviewDto;
import com.ecommerce.nashtech.modules.review.dto.GetReviewDto;
import com.ecommerce.nashtech.modules.review.error.ReviewError;
import com.ecommerce.nashtech.modules.review.model.Review;
import com.ecommerce.nashtech.modules.review.service.ReviewService;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;
import com.ecommerce.nashtech.shared.util.Router;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ReviewController implements IReviewController {

    ReviewService reviewService;
    Router router = new Router("/api/v1/reviews");

    @Override
    @PostMapping("/api/v1/reviews")
    public Mono<ResponseEntity<String>> createReview(@RequestBody CreateReviewDto dto) {
        var instance = router.getURI("");
        if (dto.rating() < 1 || dto.rating() > 5) {
            return ErrorResponse
                    .build(ReviewError.InvalidRatingError.build(dto.rating()),
                            instance)
                    .asMonoResponse(HttpStatus.BAD_REQUEST);
        }

        return reviewService.createReview(dto)
                .map(savedReview -> SuccessfulResponse.WithData.<Review>builder()
                        .item(savedReview)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(ReviewError.class,
                        error -> ErrorResponse.build(error, instance).asMonoResponse());
    }

    @Override
    @GetMapping("/api/v1/reviews/{uuid}")
    public Mono<ResponseEntity<String>> getReviewByUuid(UUID uuid) {
        var instance = router.getURI(uuid);
        return reviewService.findByUuid(uuid)
                .map(review -> SuccessfulResponse.WithData.<GetReviewDto>builder()
                        .item(review)
                        .instance(instance)
                        .build()
                        .asResponse())
                .switchIfEmpty(ErrorResponse.build(
                        ReviewError.ReviewNotFoundError.build(uuid),
                        instance)
                        .asMonoResponse(HttpStatus.NOT_FOUND));
    }

    @Override
    @GetMapping("/api/v1/reviews")
    public Mono<ResponseEntity<String>> getAllReviews() {
        var instance = router.getURI("");
        return reviewService.findAll()
                .collectList()
                .map(reviews -> SuccessfulResponse.WithData.builder()
                        .item(reviews)
                        .instance(instance)
                        .build()
                        .asResponse());
    }

    @Override
    @GetMapping("/api/v1/reviews/byProduct/{productId}")
    public Mono<ResponseEntity<String>> getReviewsByProduct(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var instance = router.getURI("product" + productId);
        var pageable = PageRequest.of(page, size);
        return reviewService.findByProductUuid(productId, pageable)
                .collectList()
                .zipWith(reviewService.countByProductUuid(productId))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(reviewPage -> SuccessfulResponse.WithPageableData.of(reviewPage, instance)
                        .asResponse())
                .onErrorResume(ReviewError.class,
                        error -> ErrorResponse.build(error, instance).asMonoResponse());
    }

    @Override
    @GetMapping("/api/v1/reviews/user/{userId}")
    public Mono<ResponseEntity<String>> getReviewsByUser(UUID userId) {
        var instance = router.getURI("user", userId);
        return reviewService.findByUserUuid(userId)
                .collectList()
                .map(reviews -> SuccessfulResponse.WithData.builder()
                        .item(reviews)
                        .instance(instance)
                        .build()
                        .asResponse());
    }

    @Override
    @GetMapping("/api/v1/reviews/byProduct/{productId}/rating")
    public Mono<ResponseEntity<String>> getProductAverageRating(UUID productId) {
        var instance = router.getURI("byProduct", productId, "rating");
        return reviewService.getAverageRatingForProduct(productId)
                .map(averageRating -> SuccessfulResponse.WithData.builder()
                        .item(averageRating)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(error -> ErrorResponse.build(
                        ReviewError.ReviewStatisticsError.build(productId),
                        instance)
                        .asMonoResponse(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    @DeleteMapping("/api/v1/reviews/{uuid}")
    public Mono<ResponseEntity<String>> deleteReview(UUID uuid) {
        var instance = router.getURI(uuid);

        return reviewService.findByUuid(uuid)
                .switchIfEmpty(Mono.error(ReviewError.ReviewNotFoundError.build(uuid)))
                .flatMap(review -> reviewService.deleteByUuid(uuid)
                        .thenReturn(SuccessfulResponse.WithMessage.builder()
                                .message("Review deleted successfully")
                                .instance(instance)
                                .build()
                                .asResponse()))
                .onErrorResume(error -> {
                    if (error instanceof ReviewError.ReviewNotFoundError) {
                        return ErrorResponse.build((ReviewError) error, instance)
                                .asMonoResponse(HttpStatus.NOT_FOUND);
                    }
                    return ErrorResponse.build(
                            ReviewError.UnauthorizedReviewModificationError
                                    .build(UUID.randomUUID(), uuid),
                            instance)
                            .asMonoResponse(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }
}