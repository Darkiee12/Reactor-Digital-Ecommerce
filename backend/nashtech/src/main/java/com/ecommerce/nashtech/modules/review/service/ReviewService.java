package com.ecommerce.nashtech.modules.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.ecommerce.nashtech.modules.review.dto.CreateReviewDto;
import com.ecommerce.nashtech.modules.review.dto.GetReviewDto;
import com.ecommerce.nashtech.modules.review.internal.repository.ReviewRepository;
import com.ecommerce.nashtech.modules.review.model.Review;
import com.ecommerce.nashtech.modules.user.service.UserService;

import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ReviewService implements IReviewService {
    ReviewRepository reviewRepo;
    UserService userService;
    TransactionalOperator txOperator;

    @Override
    public Mono<Review> createReview(CreateReviewDto dto) {
        log.debug("Creating new review for product: {}", dto.productId());
        return reviewRepo.save(dto.toReview()).as(txOperator::transactional);
    }

    @Override
    public Mono<GetReviewDto> findByUuid(UUID uuid) {
        log.debug("Finding review with UUID: {}", uuid);
        var review = reviewRepo.findByUuid(uuid);
        var fullName = userService.getFullNameByUuid(uuid);
        return Mono.zip(review, fullName)
                .map(tuple -> {
                    var reviewData = tuple.getT1();
                    var fullNameData = tuple.getT2();
                    return GetReviewDto.from(reviewData, fullNameData);
                });
    }

    public Mono<Review> findFullByUuid(UUID uuid) {
        log.debug("Finding review with UUID: {}", uuid);
        return reviewRepo.findByUuid(uuid);
    }

    @Override
    public Mono<Review> updateReview(Review review) {
        log.debug("Updating review with UUID: {}", review.getUuid());
        return reviewRepo.save(review).as(txOperator::transactional);
    }

    @Override
    public Mono<Void> deleteByUuid(UUID id) {
        log.debug("Deleting review with ID: {}", id);
        return reviewRepo.deleteByUuid(id).as(txOperator::transactional);
    }

    @Override
    public Mono<Boolean> existsByUuid(UUID uuid) {
        return reviewRepo.existsByUuid(uuid);
    }

    @Override
    public Flux<GetReviewDto> findByProductUuid(UUID productId, Pageable pageable) {
        log.debug("Finding all reviews for product: {}", productId);
        return reviewRepo.findByProductId(productId, pageable)
                .flatMap(review -> {
                    var fullName = userService.getFullNameByUuid(review.getUserId());
                    return Mono.zip(Mono.just(review), fullName);
                })
                .map(tuple -> {
                    var reviewData = tuple.getT1();
                    var fullNameData = tuple.getT2();
                    return GetReviewDto.from(reviewData, fullNameData);
                });
    }

    @Override
    public Mono<Long> countByProductUuid(UUID productId) {
        log.debug("Counting reviews for product: {}", productId);
        return reviewRepo.countByProductId(productId);
    }

    @Override
    public Flux<GetReviewDto> findByUserUuid(UUID userId) {
        log.debug("Finding all reviews by user: {}", userId);
        return reviewRepo.findByUserId(userId)
                .flatMap(review -> {
                    var fullName = userService.getFullNameByUuid(review.getUserId());
                    return Mono.zip(Mono.just(review), fullName);
                })
                .map(tuple -> {
                    var reviewData = tuple.getT1();
                    var fullNameData = tuple.getT2();
                    return GetReviewDto.from(reviewData, fullNameData);
                });
    }

    @Override
    public Mono<Double> getAverageRatingForProduct(UUID productId) {
        log.debug("Calculating average rating for product: {}", productId);
        return reviewRepo.findAllByProductId(productId)
                .map(Review::getRating)
                .collectList()
                .map(ratings -> {
                    if (ratings.isEmpty()) {
                        return 0.0;
                    }
                    double avg = ratings.stream()
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0.0);
                    return Math.round(avg * 10.0) / 10.0;
                });
    }

    @Override
    public Flux<GetReviewDto> findAll() {
        log.debug("Finding all reviews");
        return reviewRepo.findAll()
                .flatMap(review -> {
                    var fullName = userService.getFullNameByUuid(review.getUserId());
                    return Mono.zip(Mono.just(review), fullName);
                })
                .map(tuple -> {
                    var reviewData = tuple.getT1();
                    var fullNameData = tuple.getT2();
                    return GetReviewDto.from(reviewData, fullNameData);
                });
    }
}