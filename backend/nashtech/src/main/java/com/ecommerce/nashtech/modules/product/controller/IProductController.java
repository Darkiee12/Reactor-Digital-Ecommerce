package com.ecommerce.nashtech.modules.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "Product Management", description = "Operations related to product management")
public interface IProductController {

    @Operation(summary = "Retrieve product by UUID", description = "Retrieve product information by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid UUID format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getProductByUuid(
            ServerWebExchange exchange,
            @Parameter(description = "UUID of the product to retrieve", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) UUID uuid);

    @Operation(summary = "Retrieve products by brand ID", description = "Retrieve products associated with a specific brand ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid brand ID format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getProductsByBrandId(
            ServerWebExchange exchange,
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "Retrieve products by category ID", description = "Retrieve products associated with a specific category ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid category ID format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getProductsByCategoryId(
            ServerWebExchange exchange,
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);
}
