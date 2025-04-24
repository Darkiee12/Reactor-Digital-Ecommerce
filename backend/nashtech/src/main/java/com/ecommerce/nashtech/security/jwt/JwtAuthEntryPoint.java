package com.ecommerce.nashtech.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException authException) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse()
                .getHeaders()
                .set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Unauthorized");
        body.put("message", "You may login and try again!");
        body.put("timestamp", Instant.now().toString());
        body.put("path", exchange.getRequest().getPath().value());

        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = "{\"error\":\"Serialization failed\"}".getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

