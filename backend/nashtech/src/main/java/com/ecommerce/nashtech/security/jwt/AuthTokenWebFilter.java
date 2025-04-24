package com.ecommerce.nashtech.security.jwt;

import com.ecommerce.nashtech.security.user.AccountDetails;
import com.ecommerce.nashtech.security.user.AccountDetailsService;
import com.ecommerce.nashtech.shared.types.Result;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthTokenWebFilter implements WebFilter {

    private final JwtUtils jwtUtils;
    private final AccountDetailsService accountDetailsService;

    private static final List<String> WHITELIST = List.of(
        "/api/v1/account/login", 
        "/api/v1/account/register",
        "/api/v1/users",
        "/swagger-ui.html",
        "/swagger-ui/", 
        "/swagger-ui/index.html", 
        "/v3/api-docs",
        "/v3/api-docs/**", 
        "/v3/api-docs/swagger-config"
    );

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        
        if (WHITELIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        Result<String, JwtUtils.JwtParserException> res = jwtUtils.getUsernameFromToken(token);
        Mono<Void> authMono = switch (res) {
        case Result.Ok<String, JwtUtils.JwtParserException> ok -> {
            String username = ok.get();
            yield accountDetailsService.findByUsername(username).cast(AccountDetails.class).flatMap(details -> {
                Authentication auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            });
        }
        case Result.Err<String, JwtUtils.JwtParserException> err -> {
            yield handleJwtError(exchange.getResponse(), err.get());
        }
        };

        return authMono;
    }

    private Mono<Void> handleJwtError(ServerHttpResponse response, JwtUtils.JwtParserException error) {
        HttpStatus status = switch (error) {
            case JwtUtils.JwtParserException.UtilsJwtException e -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        String message = switch (error) {
            case JwtUtils.JwtParserException.UtilsJwtException e -> e.exception.getMessage()
                    + ": Invalid or expired token, please login again!";
            case JwtUtils.JwtParserException.UtilsUnsupportedJwtException e -> e.exception.getMessage();
            case JwtUtils.JwtParserException.UtilsIllegalArgumentException e -> e.exception.getMessage();
            default -> "Authentication error";
        };

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }
}
