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
    Router router = new Router("/api/v1/user");

    @Override
    @GetMapping("/uuid/{uuid}")
    public Mono<ResponseEntity<String>> getUserByUuid(
            ServerWebExchange exchange,
            @PathVariable UUID uuid) {
        var instance = router.getURI("uuid", uuid);
        var finder = new UserFinder.ByUuid(uuid);
        return userService
                .findFullUser(finder)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @GetMapping("/username/{username}")
    public Mono<ResponseEntity<String>> getUserByUsername(
            ServerWebExchange exchange,
            @PathVariable String username) {
        var instance = router.getURI("username", username);
        var finder = new UserFinder.ByUsername(username);
        return userService
                .findFullUser(finder)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @GetMapping(value = "/me")
    public Mono<ResponseEntity<String>> getCurrentUser(
            ServerWebExchange exchange) {
        var instance = router.getURI("me");

        var token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
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
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());

    }

    @Override
    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<String>> getUserByEmail(
            ServerWebExchange exchange,
            @PathVariable String email) {
        var instance = router.getURI("email", email);
        var finder = new UserFinder.ByEmail(email);
        return userService
                .findFullUser(finder)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @PostMapping()
    public Mono<ResponseEntity<String>> createUser(
            ServerWebExchange exchange,
            @RequestBody CreateUserDto dto) {
        var instance = router.getURI("create");
        return userService
                .create(dto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @PatchMapping("/uuid/{uuid}")
    public Mono<ResponseEntity<String>> updateUserByUuid(
            ServerWebExchange exchange,
            @PathVariable UUID uuid,
            @RequestBody UpdateUserDto dto) {
        var instance = router.getURI("id", uuid);
        var finder = new UserFinder.ByUuid(uuid);
        return userService
                .update(finder, dto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @PatchMapping(value = "/username/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> updateUserByUsername(
            ServerWebExchange exchange,
            @PathVariable String username,
            @RequestBody UpdateUserDto dto) {
        var instance = router.getURI("username", username);
        var finder = new UserFinder.ByUsername(username);
        return userService
                .update(finder, dto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @PatchMapping(value = "/email/{email}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> updateUserByEmail(
            ServerWebExchange exchange,
            @PathVariable String email,
            @RequestBody UpdateUserDto dto) {
        var instance = router.getURI("email", email);
        var finder = new UserFinder.ByEmail(email);
        return userService
                .update(finder, dto)
                .map(result -> SuccessfulResponse.WithData.builder()
                        .item(result)
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @DeleteMapping(value = "/uuid/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteUserByUuid(
            ServerWebExchange exchange,
            @PathVariable UUID uuid) {
        var instance = router.getURI("uuid", uuid);
        var finder = new UserFinder.ByUuid(uuid);
        return userService
                .delete(finder)
                .map(result -> SuccessfulResponse.WithMessage.builder()
                        .message("User deleted successfully")
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @DeleteMapping("/username/{username}")
    public Mono<ResponseEntity<String>> deleteUserByUsername(
            ServerWebExchange exchange,
            @PathVariable String username) {
        var instance = router.getURI("username", username);
        var finder = new UserFinder.ByUsername(username);
        return userService
                .delete(finder)
                .map(result -> SuccessfulResponse.WithMessage.builder()
                        .message("User deleted successfully")
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        e -> ErrorResponse.build(e, instance).asMonoResponse());
    }

    @Override
    @DeleteMapping("/email/{email}")
    public Mono<ResponseEntity<String>> deleteUserByEmail(
            ServerWebExchange exchange,
            @PathVariable String email) {
        var instance = router.getURI("email", email);
        var finder = new UserFinder.ByEmail(email);
        return userService
                .delete(finder)
                .map(result -> SuccessfulResponse.WithMessage.builder()
                        .message("User deleted successfully")
                        .instance(instance)
                        .build()
                        .asResponse())
                .onErrorResume(UserError.class,
                        error -> ErrorResponse.build(error, instance).asMonoResponse());
    }
}
