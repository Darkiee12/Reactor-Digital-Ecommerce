package com.ecommerce.nashtech.modules.account.controller;

import com.ecommerce.nashtech.modules.account.dto.SignInDto;
import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.modules.account.internal.response.LoginResponse;
import com.ecommerce.nashtech.security.jwt.JwtUtils;
import com.ecommerce.nashtech.shared.config.ProfileEnvironment;
import com.ecommerce.nashtech.shared.types.Option;
import com.ecommerce.nashtech.shared.util.Router;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor()
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AccountController implements IAccountController {
    ReactiveAuthenticationManager reactiveAuthenticationManager;
    ProfileEnvironment profileEnvironment;
    JwtUtils jwtUtils;
    Router router = new Router("/api/v1/account");

    @Override
    @PostMapping("/login")

    public Mono<ResponseEntity<String>> login(
        ServerWebExchange exchange,
        @Valid @RequestBody Mono<SignInDto> dtoMono
    ) {
        String instance = router.getURI("/login");

        return dtoMono
            .flatMap(dto -> {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.username(), dto.password());

                return reactiveAuthenticationManager
                    .authenticate(authToken)
                    .flatMap(auth -> {
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        String jwt = jwtUtils.generateTokenForUser(auth);
                        ResponseCookie cookie = ResponseCookie.from("token", jwt)
                                                    .httpOnly(true)
                                                    .secure(profileEnvironment.isProduction())
                                                    .path("/")
                                                    .sameSite("None")
                                                    .maxAge(Duration.ofMillis(jwtUtils.getExpirationMs()))
                                                    .build();
                        String clientIp = Option
                                            .some(exchange.getRequest().getRemoteAddress())
                                            .map(addr -> addr.getAddress().getHostAddress())
                                            .unwrapOr("unknown");
                        exchange.getResponse().addCookie(cookie);
                        exchange.getResponse().getHeaders().add("X-Client-IP", clientIp);

                        return Mono.just(ResponseEntity.ok(
                                    LoginResponse.build(jwt, jwt)));
                    });
            })
            .onErrorResume(WebExchangeBindException.class, ex -> {
                return Mono.just(ResponseEntity
                                    .badRequest()
                                    .body("Invalid input data"));
            })
            .onErrorResume(AuthenticationException.class, ex -> {
                return Mono.just(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(AccountError
                            .WrongCredentialsError
                            .build()
                            .toErrorResponse(instance)
                            .toJSON()));
            });
    }


}
