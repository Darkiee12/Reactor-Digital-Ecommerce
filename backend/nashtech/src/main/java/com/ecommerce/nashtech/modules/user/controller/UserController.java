package com.ecommerce.nashtech.modules.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.error.UserError;
import com.ecommerce.nashtech.modules.user.service.UserService;
import com.ecommerce.nashtech.shared.enums.UserFinder;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;
import com.ecommerce.nashtech.shared.util.Router;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController implements IUserController {
    UserService userService;
    Router router = new Router("/api/v1/user");

    @Override
    public Mono<ResponseEntity<String>> getUserByUuid(
        ServerWebExchange exchange,
        UUID uuid
    ){
        var instance = router.getURI("uuid", uuid);
        var finder = new UserFinder.ByUuid(uuid);
        return userService
            .find(finder)
            .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
            .onErrorResume(UserError.class, e -> Mono.just(ResponseEntity.badRequest().body(e.toErrorResponse(instance).toJSON())));
    }

    @Override
    public Mono<ResponseEntity<String>> getUserByUsername(
        ServerWebExchange exchange,
        String username
    ){
        var instance = router.getURI("username", username);
        var finder = new UserFinder.ByUsername(username);
        return userService
            .find(finder)
            .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
            .onErrorResume(UserError.class, e -> Mono.just(ResponseEntity.badRequest().body(e.toErrorResponse(instance).toJSON())));
    }

    @Override
    public Mono<ResponseEntity<String>> getUserByEmail(
        ServerWebExchange exchange,
        String email
    ){
        var instance = router.getURI("email", email);
        var finder = new UserFinder.ByEmail(email);
        return userService
            .find(finder)
            .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
            .onErrorResume(UserError.class, e -> Mono.just(ResponseEntity.badRequest().body(e.toErrorResponse(instance).toJSON())));
    }

    @Override
    public Mono<ResponseEntity<String>> createUser(
        ServerWebExchange exchange,
        CreateUserDto dto
    ){
        var instance = router.getURI("create", dto.username());
        return userService
            .create(dto)
            .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
            .onErrorResume(UserError.class, e -> Mono.just(ResponseEntity.badRequest().body(e.toErrorResponse(instance).toJSON())));
    }


    
}
