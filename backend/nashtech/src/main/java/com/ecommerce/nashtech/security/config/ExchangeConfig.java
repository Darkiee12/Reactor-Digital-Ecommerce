package com.ecommerce.nashtech.security.config;

import java.util.function.Function;

import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;

import com.ecommerce.nashtech.security.user.AccountDetails;
import com.ecommerce.nashtech.shared.enums.RoleEnum;
import com.ecommerce.nashtech.shared.util.Router;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@Component
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ExchangeConfig implements Consumer<AuthorizeExchangeSpec> {
    Router router = new Router("/api/v1");
    String[] SWAGGER_URLS = { "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/webjars/**", };
    String[] PERMITTED_URLS = { "/api/v1/account/login", "/api/v1/users" };

    @Override
    public void accept(AuthorizeExchangeSpec exchange) {
        exchange
                .pathMatchers(SWAGGER_URLS).permitAll()
                .pathMatchers(PERMITTED_URLS).permitAll()
                .pathMatchers(HttpMethod.PATCH, router.getURI("users", "username", "{username}"))
                .access((authentication, context) -> CommonAuthorizationDecision.User.build(authentication, context)
                        .byUsername())
                .pathMatchers(HttpMethod.PATCH, router.getURI("users", "uuid", "{uuid}"))
                .access((authentication, context) -> CommonAuthorizationDecision.User.build(authentication, context)
                        .byUUid())
                .pathMatchers(HttpMethod.PATCH, router.getURI("users", "email", "{email}"))
                .access((authentication, context) -> CommonAuthorizationDecision.User
                        .build(authentication, context).byEmail())
                .pathMatchers(HttpMethod.DELETE, router.getURI("users", "username", "{username}"))
                .access((authentication, context) -> CommonAuthorizationDecision.User.build(authentication, context)
                        .byUsername())
                .pathMatchers(HttpMethod.DELETE, router.getURI("users", "uuid", "{uuid}"))
                .access((authentication, context) -> CommonAuthorizationDecision.User.build(authentication, context)
                        .byUUid())
                .pathMatchers(HttpMethod.DELETE, router.getURI("users", "email", "{email}"))
                .access((authentication, context) -> CommonAuthorizationDecision.User.build(authentication, context)
                        .byEmail())

                .anyExchange().authenticated();
    }

    public String[] getPermittedUrls() {
        return PERMITTED_URLS;
    }

}

sealed interface CommonAuthorizationDecision permits CommonAuthorizationDecision.User {

    record User(Mono<Authentication> authentication, AuthorizationContext context)
            implements CommonAuthorizationDecision {

        public static User build(Mono<Authentication> authentication, AuthorizationContext context) {
            return new User(authentication, context);
        }

        private Mono<AccountDetails> getAccountDetails() {
            return authentication.map(auth -> (AccountDetails) auth.getPrincipal());
        }

        private Mono<Boolean> isAdmin() {
            return authentication.map(auth -> auth.getAuthorities().stream()
                    .anyMatch(
                            grantedAuthority -> RoleEnum.AdminRole.getName().equals(grantedAuthority.getAuthority())));
        }

        private String getPathVariable(String path) {
            return (String) context.getVariables().get(path);
        }

        private Mono<Boolean> matchesField(String fieldName,
                Function<AccountDetails, String> fieldExtractor) {
            return getAccountDetails()
                    .map(details -> fieldExtractor.apply(details).equals(getPathVariable(fieldName)));
        }

        private Mono<AuthorizationDecision> authorizeByField(String fieldName,
                Function<AccountDetails, String> fieldExtractor) {
            return Mono.zip(matchesField(fieldName, fieldExtractor), isAdmin())
                    .map(tuple -> tuple.getT1() || tuple.getT2())
                    .map(AuthorizationDecision::new);
        }

        public Mono<AuthorizationDecision> byUsername() {
            return authorizeByField("username", AccountDetails::getUsername);
        }

        public Mono<AuthorizationDecision> byUUid() {
            return authorizeByField("uuid", AccountDetails::getUuid);
        }

        public Mono<AuthorizationDecision> byEmail() {
            return authorizeByField("email", AccountDetails::getEmail);
        }
    }
}
