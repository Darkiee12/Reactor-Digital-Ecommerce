package com.ecommerce.nashtech.modules.account.controller;

import com.ecommerce.nashtech.modules.account.dto.SignInDto;
import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.modules.account.internal.response.LoginResponse;
import com.ecommerce.nashtech.security.jwt.JwtUtils;
import com.ecommerce.nashtech.shared.config.ProfileEnvironment;
import com.ecommerce.nashtech.shared.types.Option;
import com.ecommerce.nashtech.shared.types.Result;
import com.ecommerce.nashtech.shared.util.Router;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ServerWebExchange;

@RequiredArgsConstructor()
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AccountController implements IAccountController {
    AuthenticationManager authenticationManager;
    ProfileEnvironment profileEnvironment;
    JwtUtils jwtUtils;
    Router router = new Router("/api/v1/account");

    @Override

    public Mono<ResponseEntity<String>> login(ServerWebExchange exchange, @Valid @RequestBody SignInDto dto,
            BindingResult result) {
        final var instance = router.getURI("/login");
        return Mono.defer(() -> {
            var _result = Result.fromBindingResult(result);
            if (_result.isErr()) {
                return Mono.just(ResponseEntity.badRequest().body("Invalid input data"));
            }
            return Mono
                    .fromCallable(() -> authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(dto.username(), dto.password())))
                    .flatMap(auth -> {
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        return Mono.just(auth);
                    }).flatMap(auth -> {
                        String jwt = jwtUtils.generateTokenForUser(auth);
                        ResponseCookie cookie = ResponseCookie.from("token", jwt).httpOnly(true)
                                .secure(profileEnvironment.isProduction()).path("/").sameSite("None")
                                .maxAge(Duration.ofMillis(jwtUtils.getExpirationMs())) // Set expiration
                                .build();
                        String clientIp = Option.some(exchange.getRequest().getRemoteAddress())
                                .map(addr -> addr.getAddress().getHostAddress()).unwrapOr("unknown");
                        exchange.getResponse().addCookie(cookie);
                        exchange.getResponse().getHeaders().add("X-Client-IP", clientIp);
                        return Mono.just(ResponseEntity.ok(LoginResponse.build(jwt, jwt)));
                    }).onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(AccountError.WrongCredentialsError.build().toErrorResponse(instance).toJSON())));
        });
    }
}
