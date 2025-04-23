package com.ecommerce.nashtech.modules.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Tag(name = "User Management", description = "Operations related to user management")
public interface IUserController {
    @GetMapping("/uuid/{uuid}")
    public Mono<ResponseEntity<String>> getUserByUuid(
        ServerWebExchange exchange,
        @Parameter(description = "UUID of the account to be retrieved", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) 
        @PathVariable UUID uuid
    );

    @GetMapping("/username/{username}")
    public Mono<ResponseEntity<String>> getUserByUsername(
        ServerWebExchange exchange,
        @Parameter(description = "UUID of the account to be retrieved", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) 
        @PathVariable String username
    );

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<String>> getUserByEmail(
        ServerWebExchange exchange,
        @Parameter(description = "UUID of the account to be retrieved", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) 
        @PathVariable String email
    );

    @PostMapping()
    public Mono<ResponseEntity<String>> createUser(
        ServerWebExchange exchange,
        @Parameter(description = "UUID of the account to be retrieved", required = true, schema = @Schema(type = "string", format = "uuid", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")) 
        @RequestBody CreateUserDto dto
    );
    
}
