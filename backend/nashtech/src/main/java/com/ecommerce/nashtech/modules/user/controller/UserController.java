package com.ecommerce.nashtech.modules.user.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.ecommerce.nashtech.modules.user.dto.CreateUserDto;
import com.ecommerce.nashtech.modules.user.dto.UpdateUserDto;
import com.ecommerce.nashtech.modules.user.error.UserError;
import com.ecommerce.nashtech.modules.user.service.UserService;
import com.ecommerce.nashtech.shared.enums.UserFinder;
import com.ecommerce.nashtech.shared.json.JSON;
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
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController implements IUserController {
    UserService userService;
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
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
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
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
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
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
    }

    @Override
    @PostMapping()
    public Mono<ResponseEntity<String>> createUser(
            ServerWebExchange exchange,
            @RequestBody CreateUserDto dto) {
        var instance = router.getURI("create");
        return userService
                .create(dto)
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_USER') and authentication.principal.uuid = #uuid")
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
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
    }

    // @PreAuthorize("hasAuthority('ROLE_USER')")
    // @Secured({ "ROLE_USER" })
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
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_USER') and authentication.principal.email = #email  ")
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
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_USER') and authentication.principal.uuid = #uuid")
    @Override
    @DeleteMapping(value = "/uuid/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteUserByUuid(
            ServerWebExchange exchange,
            @PathVariable UUID uuid) {
        var instance = router.getURI("uuid", uuid);
        var finder = new UserFinder.ByUuid(uuid);
        return userService
                .delete(finder)
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_USER') and authentication.principal.username = #username")
    @Override
    @DeleteMapping("/username/{username}")
    public Mono<ResponseEntity<String>> deleteUserByUsername(
            ServerWebExchange exchange,
            @PathVariable String username) {
        var instance = router.getURI("username", username);
        var finder = new UserFinder.ByUsername(username);
        return userService
                .delete(finder)
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_USER') and authentication.principal.email = #email")
    @Override
    @DeleteMapping("/email/{email}")
    public Mono<ResponseEntity<String>> deleteUserByEmail(
            ServerWebExchange exchange,
            @PathVariable String email) {
        var instance = router.getURI("email", email);
        var finder = new UserFinder.ByEmail(email);
        return userService
                .delete(finder)
                .map(result -> ResponseEntity.ok(SuccessfulResponse.build(result, instance)))
                .onErrorResume(UserError.class,
                        e -> Mono.just(ResponseEntity.badRequest()
                                .body(e.toErrorResponse(instance).toJSON())));
    }

}
