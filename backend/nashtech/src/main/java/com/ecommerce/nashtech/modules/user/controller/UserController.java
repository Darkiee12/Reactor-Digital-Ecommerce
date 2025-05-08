package com.ecommerce.nashtech.modules.user.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.dto.UpdateUserDto;
import com.ecommerce.nashtech.modules.user.error.UserError;
import com.ecommerce.nashtech.modules.user.service.UserService;
import com.ecommerce.nashtech.security.jwt.JwtUtils;
import com.ecommerce.nashtech.shared.enums.UserFinder;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;
import com.ecommerce.nashtech.shared.util.Router;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/users")
// @CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController implements IUserController {
    UserService userService;
    JwtUtils.Token.AccessTokenProvider accessTokenProvider;
    Router router = new Router("/api/v1/users");

    @Override
    @GetMapping("/{uuid}")
    public Mono<ResponseEntity<String>> getUserByUuid(
            ServerWebExchange exchange,
            @PathVariable UUID uuid) {
        String instance = router.getURI("uuid", uuid);
        log.info("Fetching user by UUID: {}", uuid);

        var finder = new UserFinder.ByUuid(uuid);
        return userService
                .findFullUser(finder)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error fetching user by UUID {}: {}", uuid,
                                    e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @GetMapping("/byUsername/{username}")
    public Mono<ResponseEntity<String>> getUserByUsername(
            ServerWebExchange exchange,
            @PathVariable String username) {
        String instance = router.getURI("username", username);
        log.info("Fetching user by username: {}", username);

        var finder = new UserFinder.ByUsername(username);
        return userService
                .findFullUser(finder)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error fetching user by username {}: {}", username,
                                    e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @GetMapping("/me")
    public Mono<ResponseEntity<String>> getCurrentUser(ServerWebExchange exchange) {
        String instance = router.getURI("me");
        log.info("Fetching current user information");

        var token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Unauthorized access attempt to /me endpoint");
            return ErrorResponse
                    .build(AccountError.UnauthorizedError.build(), instance)
                    .asMonoResponse();
        }

        var jwt = token.substring(7);
        return accessTokenProvider.getUsernameFromToken(jwt)
                .map(username -> new UserFinder.ByUsername(username))
                .flatMap(finder -> userService.findFullUser(finder))
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error fetching current user: {}", e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @GetMapping("/byEmail/{email}")
    public Mono<ResponseEntity<String>> getUserByEmail(
            ServerWebExchange exchange,
            @PathVariable String email) {
        String instance = router.getURI("email", email);
        log.info("Fetching user by email: {}", email);

        var finder = new UserFinder.ByEmail(email);
        return userService
                .findFullUser(finder)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error fetching user by email {}: {}", email,
                                    e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createUser(
            ServerWebExchange exchange,
            @RequestBody CreateUserDto dto) {
        String instance = router.getURI("create");
        log.info("Creating new user with username: {}", dto.username());

        return userService
                .create(dto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error creating user {}: {}", dto.username(),
                                    e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @PatchMapping("/{uuid}")
    public Mono<ResponseEntity<String>> updateUserByUuid(
            ServerWebExchange exchange,
            @PathVariable UUID uuid,
            @RequestBody UpdateUserDto dto) {
        String instance = router.getURI("uuid", uuid);
        log.info("Updating user with UUID: {}", uuid);

        var finder = new UserFinder.ByUuid(uuid);
        return userService
                .update(finder, dto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error updating user {}: {}", uuid, e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @PatchMapping(value = "/byUsername/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> updateUserByUsername(
            ServerWebExchange exchange,
            @PathVariable String username,
            @RequestBody UpdateUserDto dto) {
        String instance = router.getURI("username", username);
        log.info("Updating user with username: {}", username);

        var finder = new UserFinder.ByUsername(username);
        return userService
                .update(finder, dto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error updating user {}: {}", username,
                                    e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @PatchMapping(value = "/byEmail/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> updateUserByEmail(
            ServerWebExchange exchange,
            @PathVariable String email,
            @RequestBody UpdateUserDto dto) {
        String instance = router.getURI("email", email);
        log.info("Updating user with email: {}", email);

        var finder = new UserFinder.ByEmail(email);
        return userService
                .update(finder, dto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error updating user {}: {}", email, e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @DeleteMapping("/{uuid}")
    public Mono<ResponseEntity<String>> deleteUserByUuid(
            ServerWebExchange exchange,
            @PathVariable UUID uuid) {
        String instance = router.getURI("uuid", uuid);
        log.info("Deleting user with UUID: {}", uuid);

        var finder = new UserFinder.ByUuid(uuid);
        return userService
                .delete(finder)
                .map(result -> SuccessfulResponse.WithMessage.builder()
                        .message("User deleted successfully")
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error deleting user {}: {}", uuid, e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @DeleteMapping("/byUsername/{username}")
    public Mono<ResponseEntity<String>> deleteUserByUsername(
            ServerWebExchange exchange,
            @PathVariable String username) {
        String instance = router.getURI("username", username);
        log.info("Deleting user with username: {}", username);

        var finder = new UserFinder.ByUsername(username);
        return userService
                .delete(finder)
                .map(result -> SuccessfulResponse.WithMessage.builder()
                        .message("User deleted successfully")
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error deleting user {}: {}", username,
                                    e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }

    @Override
    @DeleteMapping("/byEmail/{email}")
    public Mono<ResponseEntity<String>> deleteUserByEmail(
            ServerWebExchange exchange,
            @PathVariable String email) {
        String instance = router.getURI("email", email);
        log.info("Deleting user with email: {}", email);

        var finder = new UserFinder.ByEmail(email);
        return userService
                .delete(finder)
                .map(result -> SuccessfulResponse.WithMessage.builder()
                        .message("User deleted successfully")
                        .instance(instance)
                        .build()
                        .asMonoResponse())
                .flatMap(response -> response)
                .onErrorResume(UserError.class,
                        e -> {
                            log.error("Error deleting user {}: {}", email, e.getMessage());
                            return ErrorResponse.build(e, instance).asMonoResponse();
                        });
    }
}
