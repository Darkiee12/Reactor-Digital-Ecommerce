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

@Component
@RequiredArgsConstructor
public class AuthTokenWebFilter implements WebFilter {

    private final JwtUtils jwtUtils;
    private final AccountDetailsService accountDetailsService;

    @Override

    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        Result<String, JwtUtils.JwtParserException> res = jwtUtils.getUsernameFromToken(token);

        // use a switch expression to *yield* exactly one Mono<Void>
        Mono<Void> authMono = switch (res) {
        case Result.Ok<String, JwtUtils.JwtParserException> ok -> {
            String username = ok.get();
            // find the user, then flatMap into the filter chain
            yield accountDetailsService.findByUsername(username).cast(AccountDetails.class).flatMap(details -> {
                Authentication auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                // chain.filter(exchange) already returns Mono<Void>
                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            });
        }
        case Result.Err<String, JwtUtils.JwtParserException> err -> {
            // handleJwtError also returns a non‐null Mono<Void>
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
