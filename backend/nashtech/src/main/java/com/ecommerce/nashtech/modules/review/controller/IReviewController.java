package com.ecommerce.nashtech.modules.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.nashtech.modules.review.dto.CreateReviewDto;
import com.ecommerce.nashtech.modules.review.model.Review;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "Review Management", description = "Operations related to product reviews")
public interface IReviewController {

    @Operation(summary = "Create new review", description = "Create a new product review with rating and comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid review data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> createReview(@RequestBody CreateReviewDto dto);

    @Operation(summary = "Retrieve review by UUID", description = "Get a specific review by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getReviewByUuid(
            @Parameter(description = "UUID of the review to retrieve", required = true, schema = @Schema(type = "string", format = "uuid")) @PathVariable UUID uuid);

    @Operation(summary = "Retrieve all reviews", description = "Get a list of all product reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getAllReviews();

    @Operation(summary = "Retrieve reviews by product", description = "Get all reviews for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ResponseEntity<String>> getReviewsByProduct(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "Retrieve reviews by user", description = "Get all reviews created by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getReviewsByUser(
            @Parameter(description = "UUID of the user", required = true, schema = @Schema(type = "string", format = "uuid")) @PathVariable UUID userId);

    @Operation(summary = "Get product average rating", description = "Calculate the average rating for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Average rating retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getProductAverageRating(
            @Parameter(description = "UUID of the product", required = true, schema = @Schema(type = "string", format = "uuid")) @PathVariable UUID productId);

    @Operation(summary = "Delete review", description = "Delete a review by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithMessage.class))),
            @ApiResponse(responseCode = "404", description = "Review not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> deleteReview(
            @Parameter(description = "UUID of the review to delete", required = true, schema = @Schema(type = "string", format = "uuid")) @PathVariable UUID uuid);
}