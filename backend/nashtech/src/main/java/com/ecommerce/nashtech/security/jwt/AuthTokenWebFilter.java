package com.ecommerce.nashtech.security.jwt;

import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.security.user.AccountDetails;
import com.ecommerce.nashtech.security.user.AccountDetailsService;
import com.ecommerce.nashtech.shared.json.JSON;
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
    JwtUtils.Token.RefreshTokenProvider refreshToken;
    AccountDetailsService accountDetailsService;

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange,
            @NonNull WebFilterChain chain) {
        Set<String> PERMITTED_URLS = Set.of("/api/v1/account/login", "/api/v1/users");
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String requestPath = exchange.getRequest().getPath().value();
        if (!StringUtils.hasText(authHeader) || authHeader == null
                || !authHeader.startsWith("Bearer ") || PERMITTED_URLS.contains(requestPath)) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        return accessTokenProvider.getUsernameFromToken(token).flatMap(username -> accountDetailsService
                .findByUsername(username).cast(AccountDetails.class).flatMap(details -> {
                    Authentication auth = new UsernamePasswordAuthenticationToken(details, null,
                            details.getAuthorities());
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                })).onErrorResume(e -> handleJwtError(exchange.getResponse(), e));
    }

    private Mono<Void> handleJwtError(ServerHttpResponse response, Throwable error) {
        HttpStatus status;
        String message;

        if (error instanceof AccountError.InvalidTokenError) {
            status = HttpStatus.UNAUTHORIZED;
            message = error.getMessage();
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
}
