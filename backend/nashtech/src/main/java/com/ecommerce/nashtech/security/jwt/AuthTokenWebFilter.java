package com.ecommerce.nashtech.security.jwt;

import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.security.user.AccountDetails;
import com.ecommerce.nashtech.security.user.AccountDetailsService;
import com.ecommerce.nashtech.shared.error.BaseError;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AuthTokenWebFilter implements WebFilter {

    JwtUtils.Token.AccessTokenProvider accessTokenProvider;
    AccountDetailsService accountDetailsService;
    Set<String> permittedUrls = Set.of("/api/v1/auth/login", "/api/v1/auth/refresh", "/swagger-ui.html",
            "/v3/api-docs/**", "/v2/api-docs/**", "/webjars/**", "/swagger-resources/**", "/actuator/**");

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange,
            @NonNull WebFilterChain chain) {

        String token = parseJWT(exchange);
        if (permittedUrls.contains(exchange.getRequest().getPath().value())) {
            return chain.filter(exchange);
        }
        if (token == null || !StringUtils.hasText(token)) {
            return chain.filter(exchange);
        }
        return accessTokenProvider.validateToken(token)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(AccountError.ExpiredTokenError.build()))
                .then(accessTokenProvider.getUsernameFromToken(token))
                .flatMap(username -> accountDetailsService
                        .findByUsername(username)
                        .cast(AccountDetails.class)
                        .flatMap(details -> {
                            Authentication auth = new UsernamePasswordAuthenticationToken(details, null,
                                    details.getAuthorities());
                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                        }))
                .onErrorResume(e -> handleJwtError(exchange.getResponse(), e));
    }

    private Mono<Void> handleJwtError(ServerHttpResponse response, Throwable error) {
        HttpStatus status;
        String message;
        System.out.println("JWT error: " + error.getMessage());
        if (error instanceof AccountError.InvalidTokenError) {
            status = HttpStatus.UNAUTHORIZED;
            message = ((BaseError) error).toJSON();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Authentication error";
        }

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    private String parseJWT(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
