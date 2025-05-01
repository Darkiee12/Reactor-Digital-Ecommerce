package com.ecommerce.nashtech.security.jwt;

import com.ecommerce.nashtech.security.user.AccountDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import com.ecommerce.nashtech.modules.account.dto.FullAccountDto;
import com.ecommerce.nashtech.modules.account.error.*;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
    public static abstract class Token {
        String jwtSecret;
        long expirationInMillis;
        @NonFinal
        volatile SecretKey secretKey;

        public Token(String jwtSecret, long expirationInMillis) {
            this.jwtSecret = jwtSecret;
            this.expirationInMillis = expirationInMillis;
            if (this.secretKey == null) {
                synchronized (this) {
                    if (this.secretKey == null) {
                        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
                        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
                    }
                }
            }
        }

        public SecretKey getSecretKey() {
            return this.secretKey;
        }

        public long getExpirationMs() {
            return this.expirationInMillis;
        }

        public Mono<Jws<Claims>> parseClaims(String token) {
            return Mono.fromCallable(() -> {
                JwtParser parser = Jwts.parser()
                        .verifyWith(getSecretKey())
                        .build();
                return parser.parseSignedClaims(token);
            })
                    .onErrorMap(JwtException.class, e -> AccountError.InvalidTokenError.build())
                    .onErrorMap(IllegalArgumentException.class, e -> AccountError.InvalidTokenError.build());
        }

        public Mono<Boolean> validateToken(String token) {
            return parseClaims(token)
                    .map(jws -> {
                        Date expiration = jws.getPayload().getExpiration();
                        return expiration != null && expiration.after(new Date());
                    })
                    .onErrorMap(e -> AccountError.InvalidTokenError.build());
        }

        @Component
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class AccessTokenProvider extends Token {

            public AccessTokenProvider(
                    @Value("${auth.accessToken.jwtSecret}") String jwtSecretForAccessToken,
                    @Value("${auth.accessToken.expirationInMillis}") long expirationInMillis) {
                super(jwtSecretForAccessToken, expirationInMillis);
            }

            public String generateToken(Authentication authentication) {
                AccountDetails account = (AccountDetails) authentication.getPrincipal();
                List<String> roles = account.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
                Date now = new Date();
                Date expiry = new Date(now.getTime() + this.expirationInMillis);
                return Jwts.builder()
                        .subject(account.getUsername())
                        .id(account.getUuid().toString())
                        .claim("roles", roles)
                        .issuedAt(now)
                        .notBefore(now)
                        .expiration(expiry)
                        .signWith(getSecretKey())
                        .compact();
            }

            public String generateToken(FullAccountDto dto) {
                Date now = new Date();
                Date expiry = new Date(now.getTime() + this.expirationInMillis);
                return Jwts.builder()
                        .subject(dto.username())
                        .id(dto.uuid().toString())
                        .claim("roles", dto.getRoles())
                        .issuedAt(now)
                        .notBefore(now)
                        .expiration(expiry)
                        .signWith(getSecretKey())
                        .compact();
            }

            public Mono<String> getUsernameFromToken(String token) {
                return parseClaims(token)
                        .map(jws -> jws.getPayload().getSubject());
            }
        }

        @Component
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class RefreshTokenProvider extends Token {

            public RefreshTokenProvider(
                    @Value("${auth.refreshToken.jwtSecret}") String jwtSecretForRefreshToken,
                    @Value("${auth.refreshToken.expirationInMillis}") long expirationInMillis) {
                super(jwtSecretForRefreshToken, expirationInMillis);
            }

            public String generateToken(Authentication authentication) {
                AccountDetails account = (AccountDetails) authentication.getPrincipal();
                Date now = new Date();
                Date expiry = new Date(now.getTime() + this.expirationInMillis);
                return Jwts.builder()
                        .subject(account.getUsername())
                        .id(account.getUuid().toString())
                        .issuedAt(now)
                        .notBefore(now)
                        .expiration(expiry)
                        .signWith(getSecretKey())
                        .compact();
            }

            public Mono<UUID> getUuidFromToken(String token) {
                return parseClaims(token)
                        .map(jws -> jws.getPayload().getId())
                        .map(UUID::fromString)
                        .onErrorMap(IllegalArgumentException.class,
                                e -> AccountError.InvalidTokenError.build());

            }
        }

    }
}
