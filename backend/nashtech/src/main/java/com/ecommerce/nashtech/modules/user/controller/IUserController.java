package com.ecommerce.nashtech.modules.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.dto.UpdateUserDto;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Tag(name = "User Management", description = "Operations related to user management")
public interface IUserController {

    @Operation(summary = "Retrieve user by UUID", description = "Retrieve user information by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid UUID format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> getUserByUuid(
            ServerWebExchange exchange,
            @Parameter(description = "UUID of the user to retrieve", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) UUID uuid);

    @Operation(summary = "Retrieve user by username", description = "Retrieve user information by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class), examples = {
                    @ExampleObject(name = "InvalidUsername", value = ErrorResponse.Example)
            })),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> getUserByUsername(
            ServerWebExchange exchange,
            @Parameter(description = "Username of the user to retrieve", required = true, schema = @Schema(type = "string", example = "username")) String username);

    @Operation(summary = "Retrieve user by email", description = "Retrieve user information by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> getUserByEmail(
            ServerWebExchange exchange,
            @Parameter(description = "Email of the user to retrieve", required = true, schema = @Schema(type = "string", format = "email", example = "user@email.com")) String email);

    @Operation(summary = "Create a new user", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> createUser(
            ServerWebExchange exchange,
            @Parameter(description = "User information to create", required = true, schema = @Schema(implementation = CreateUserDto.class)) CreateUserDto dto);

    @Operation(summary = "Update user information", description = "Update user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> updateUserByUuid(
            ServerWebExchange exchange,
            @Parameter(description = "UUID of the user to update", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) UUID uuid,
            @Parameter(description = "User information to update", required = true, schema = @Schema(implementation = UpdateUserDto.class)) UpdateUserDto dto);

    @Operation(summary = "Update user information", description = "Update user information")
    public Mono<ResponseEntity<String>> updateUserByUsername(
            ServerWebExchange exchange,
            @Parameter(description = "Username of the user to update", required = true, schema = @Schema(type = "string", example = "username")) String username,
            @Parameter(description = "User information to update", required = true, schema = @Schema(implementation = UpdateUserDto.class)) UpdateUserDto dto);

    @Operation(summary = "Update user by email", description = "Update user information by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> updateUserByEmail(
            ServerWebExchange exchange,
            @Parameter(description = "Email of the user to update", required = true, schema = @Schema(type = "string", format = "email", example = "user@email.com")) String email,
            @Parameter(description = "User information to update", required = true, schema = @Schema(implementation = UpdateUserDto.class)) UpdateUserDto dto);

    @Operation(summary = "Delete user by UUID", description = "Delete user by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid UUID format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> deleteUserByUuid(
            ServerWebExchange exchange,
            UUID uuid);

    @Operation(summary = "Delete user by username", description = "Delete user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> deleteUserByUsername(
            ServerWebExchange exchange,
            @Parameter(description = "Username of the user to delete", required = true, schema = @Schema(type = "string", example = "username")) String username);

    @Operation(summary = "Delete user by email", description = "Delete user by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.ErrorResponseObject.class)))
    })
    public Mono<ResponseEntity<String>> deleteUserByEmail(
            ServerWebExchange exchange,
            @Parameter(description = "Email of the user to delete", required = true, schema = @Schema(type = "string", format = "email")) String email);

}
