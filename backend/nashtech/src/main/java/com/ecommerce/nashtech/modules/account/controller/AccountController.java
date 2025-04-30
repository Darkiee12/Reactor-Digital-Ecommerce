package com.ecommerce.nashtech.modules.account.controller;

import com.ecommerce.nashtech.modules.account.dto.SignInDto;
import com.ecommerce.nashtech.modules.account.error.AccountError;
import com.ecommerce.nashtech.modules.account.service.AccountService;
import com.ecommerce.nashtech.security.jwt.JwtUtils;
import com.ecommerce.nashtech.shared.config.ProfileEnvironment;
import com.ecommerce.nashtech.shared.enums.UserFinder;
import com.ecommerce.nashtech.shared.response.ErrorResponse;
import com.ecommerce.nashtech.shared.response.SuccessfulResponse;
import com.ecommerce.nashtech.shared.types.Option;
import com.ecommerce.nashtech.shared.util.Router;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AccountController implements IAccountController {

    ReactiveAuthenticationManager reactiveAuthenticationManager;
    ProfileEnvironment profileEnvironment;
    AccountService accountService;
    JwtUtils.Token.AccessTokenProvider accessTokenProvider;
    JwtUtils.Token.RefreshTokenProvider refreshTokenProvider;
    Router router = new Router("/api/v1/auth");

    @Override
    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(
            ServerWebExchange exchange,
            @Valid @RequestBody Mono<SignInDto> dtoMono) {

        String instance = router.getURI("login");

        return dtoMono
                .flatMap(dto -> authenticate(dto))
                .flatMap(auth -> generateLoginResponse(exchange, auth, instance))
                .onErrorResume(WebExchangeBindException.class, ex -> badRequestResponse())
                .onErrorResume(AuthenticationException.class, ex -> unauthorizedResponse(instance));
    }

    @Override
    @GetMapping("/refresh")
    public Mono<ResponseEntity<String>> renewAccessToken(ServerWebExchange exchange) {
        String instance = router.getURI("refresh");

        MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
        Option<HttpCookie> refreshTokenCookie = Option.fromNullable(cookies.getFirst("refreshToken"));
        return switch (refreshTokenCookie) {
            case Option.Some<HttpCookie> someCookie -> {
                var cookie = someCookie.get();
                String refreshToken = cookie.getValue();
                yield refreshTokenProvider
                        .getUuidFromToken(refreshToken)
                        .map(uuid -> new UserFinder.ByUuid(uuid))
                        .flatMap(finder -> accountService.findFullAccount(finder))
                        .map(fullAccount -> accessTokenProvider.generateToken(fullAccount))
                        .map(accessJwt -> Map.of("accessToken", accessJwt))
                        .map(accessToken -> SuccessfulResponse.WithData.builder().item(accessToken).build()
                                .asResponse())
                        .onErrorResume(AccountError.InvalidTokenError.class,
                                e -> unauthorizedResponse(instance))
                        .onErrorResume(AccountError.class, e -> ErrorResponse.build(e, instance).asMonoResponse());

            }
            case Option.None<HttpCookie> none -> {
                yield unauthorizedResponse(instance);
            }
        };
    }

    @Override
    @GetMapping("/logout")
    public Mono<ResponseEntity<String>> logout(ServerWebExchange exchange) {
        String instance = router.getURI("logout");

        // Create an expired refreshToken cookie
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(profileEnvironment.isProduction())
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ZERO) // Delete the cookie
                .build();

        exchange.getResponse().addCookie(expiredCookie);

        return SuccessfulResponse.WithMessage.builder().message("Logout successful")
                .instance(instance)
                .build()
                .asMonoResponse();
    }

    // ==== Private helper methods ====

    private Mono<UsernamePasswordAuthenticationToken> createAuthToken(SignInDto dto) {
        return Mono.just(new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));
    }

    private Mono<Authentication> authenticate(SignInDto dto) {
        return createAuthToken(dto)
                .flatMap(reactiveAuthenticationManager::authenticate)
                .doOnNext(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
    }

    private Mono<ResponseEntity<String>> generateLoginResponse(ServerWebExchange exchange, Authentication auth,
            String instance) {
        String accessJwt = accessTokenProvider.generateToken(auth);
        String refreshJwt = refreshTokenProvider.generateToken(auth);

        ResponseCookie cookie = createRefreshCookie(refreshJwt);

        exchange.getResponse().addCookie(cookie);
        String clientIp = extractClientIp(exchange);
        exchange.getResponse().getHeaders().add("X-Client-IP", clientIp);

        var accessToken = Map.of("accessToken", accessJwt);
        return SuccessfulResponse.WithData.builder()
                .item(accessToken)
                .instance(instance)
                .build()
                .asMonoResponse();
    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(profileEnvironment.isProduction())
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(accessTokenProvider.getExpirationMs()))
                .build();
    }

    private String extractClientIp(ServerWebExchange exchange) {
        return Option
                .some(exchange.getRequest().getRemoteAddress())
                .map(addr -> addr.getAddress().getHostAddress())
                .unwrapOr("unknown");
    }

    private Mono<ResponseEntity<String>> badRequestResponse() {
        return Mono.just(ResponseEntity
                .badRequest()
                .body("Invalid input data"));
    }

    private Mono<ResponseEntity<String>> unauthorizedResponse(String instance) {
        return ErrorResponse.build(AccountError.WrongCredentialsError.build(), instance)
                .asMonoResponse(HttpStatus.UNAUTHORIZED);
    }
}
