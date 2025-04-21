package com.ecommerce.nashtech.security.jwt;
import com.ecommerce.nashtech.security.user.AccountDetails;
import com.ecommerce.nashtech.utils.rust.Result;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.expirationInMils}")
    private long expirationInMillis;


    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateTokenForUser(Authentication authentication) {
        AccountDetails account = (AccountDetails) authentication.getPrincipal();

        List<String> roles = account.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        SecretKey key = getSecretKey();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationInMillis);
        return Jwts.builder()
                .subject(account.getUsername())
                .id(account.getUuid().toString())
                .claim("roles", roles)
                .issuedAt(now)
                .notBefore(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }    

    public Result<String, JwtParserException> getUsernameFromToken(String token) {
        SecretKey key = getSecretKey();
        return JwtParserException.wrap(
            Jwts.parser()
                .verifyWith(key)
                .build(), token)
            .map(claims -> claims.getPayload().getSubject());       
    }

    public static sealed class JwtParserException permits 
        JwtParserException.UtilsUnsupportedJwtException, 
        JwtParserException.UtilsJwtException, 
        JwtParserException.UtilsIllegalArgumentException {
        public Exception exception;
        private JwtParserException() {
            super();
        }
        public static final class UtilsUnsupportedJwtException extends JwtParserException{
            public final UnsupportedJwtException exception = new UnsupportedJwtException("Unsupported JWT");
            public UtilsUnsupportedJwtException() {
                super();
            }
        }
        public static final class UtilsJwtException extends JwtParserException {

            public final JwtException exception = new JwtException("JWT error");
            public UtilsJwtException() {
                super();
            }
        }
        public static final class UtilsIllegalArgumentException extends JwtParserException {
            public final IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");
            public UtilsIllegalArgumentException() {
                super();
            }
        }

        
        public static Result<Jwe<Claims>,JwtParserException> wrap(JwtParser parser, CharSequence jwe) {
            try{
                var claims = parser.parseEncryptedClaims(jwe);
                return Result.ok(claims);
            } catch (UnsupportedJwtException e) {
                return Result.err(new UtilsUnsupportedJwtException());
                
            } catch (JwtException e) {
                return Result.err(new UtilsJwtException());
            } catch (IllegalArgumentException e) {
                return Result.err(new UtilsIllegalArgumentException());
            } catch (Exception e) {
                return Result.err(new JwtParserException());
            } 
            
        }

    }

    public boolean validateToken(String token) {
        SecretKey key = getSecretKey();
        Result<Jwe<Claims>, JwtParserException> result = JwtParserException.wrap(
            Jwts.parser()
                .verifyWith(key)
                .build(), token);
        switch(result){
            case Result.Ok<Jwe<Claims>, JwtParserException> ok -> {
                var claims = ok.get();
                return !claims.getPayload().getExpiration().before(new Date());
            }
            case Result.Err<Jwe<Claims>, JwtParserException> err -> {
                JwtParserException error = err.get();
                switch (error) {
                    case JwtParserException.UtilsJwtException e -> {
                        return false;
                    }
                    case JwtParserException.UtilsUnsupportedJwtException e -> {
                        return false;
                    }
                    case JwtParserException.UtilsIllegalArgumentException e -> {
                        return false;
                    }
                    default -> {
                        return false;
                    }
                }
            }
        }
        
    }

    public long getExpirationMs() {
        return expirationInMillis;
    }
}
