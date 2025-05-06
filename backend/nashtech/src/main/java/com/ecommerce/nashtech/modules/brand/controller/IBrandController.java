package com.ecommerce.nashtech.modules.brand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.modules.brand.dto.BrandDto;
import com.ecommerce.nashtech.modules.brand.dto.CreateBrandDto;
import com.ecommerce.nashtech.modules.brand.dto.UpdateBrandDto;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Tag(name = "Brand Management", description = "Operations related to brand management")
public interface IBrandController {

    @Operation(summary = "Get all brands", description = "Retrieve all brands with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brands retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getAll(
            @Parameter(description = "Page number (zero-based)", schema = @Schema(type = "integer", defaultValue = "0")) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", schema = @Schema(type = "integer", defaultValue = "10")) @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "Get brand by ID", description = "Retrieve a brand by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> getById(
            @Parameter(description = "ID of the brand to retrieve", required = true, schema = @Schema(type = "integer", format = "int64")) @PathVariable Long id);

    @Operation(summary = "Create a new brand", description = "Create a new brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> create(
            @RequestBody Mono<CreateBrandDto> dto);

    @Operation(summary = "Update brand", description = "Update an existing brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> update(
            @Parameter(description = "ID of the brand to update", required = true, schema = @Schema(type = "integer", format = "int64")) @PathVariable Long id,
            @RequestBody UpdateBrandDto dto);

    @Operation(summary = "Delete brand", description = "Delete a brand by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithMessage.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> delete(
            @Parameter(description = "ID of the brand to delete", required = true, schema = @Schema(type = "integer", format = "int64")) @PathVariable Long id);

    @Operation(summary = "Upload brand image", description = "Upload an image for a brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithMessage.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    Mono<ResponseEntity<String>> uploadBrandImage(
            ServerWebExchange exchange,
            @RequestPart("file") Mono<FilePart> filePart,
            @PathVariable Long brandId,
            @RequestPart("altText") String altText);

    @Operation(summary = "Search brands", description = "Search for brands by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brands retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.WithData.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameter(description = "Search term to filter brands by name", required = true, schema = @Schema(type = "string"))
    @Parameter(description = "Page number (zero-based)", schema = @Schema(type = "integer", defaultValue = "0"))
    @Parameter(description = "Number of items per page", schema = @Schema(type = "integer", defaultValue = "10"))
    Mono<ResponseEntity<String>> search(@RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);
}
